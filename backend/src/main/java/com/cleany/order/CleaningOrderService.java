package com.cleany.order;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.configuration.CleanerProperties;
import com.cleany.configuration.CleaningProperties;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.pricing.CleaningPriceService;
import com.cleany.referral.OrderReferralPlan;
import com.cleany.referral.ReferralService;

@Service
public class CleaningOrderService {

    private static final List<CleaningOrderStatus> ACTIVE_STATUSES = List.of(
            CleaningOrderStatus.NEW,
            CleaningOrderStatus.ACCEPTED,
            CleaningOrderStatus.AWAITING_REPORT,
            CleaningOrderStatus.ONSITE_ISSUE_REPORTED
    );

    private final CleaningOrderRepository orderRepository;
    private final CleaningOrderPhotoRepository photoRepository;
    private final CleaningOrderEventRepository orderEventRepository;
    private final CleaningPriceService priceService;
    private final PhoneNumberNormalizer phoneNumberNormalizer;
    private final CleaningProperties cleaningProperties;
    private final CleanerProperties cleanerProperties;
    private final CustomerAccountService customerAccountService;
    private final ReferralService referralService;
    private final Clock clock;
    private final ApplicationEventPublisher eventPublisher;

    public CleaningOrderService(
            CleaningOrderRepository orderRepository,
            CleaningOrderPhotoRepository photoRepository,
            CleaningOrderEventRepository orderEventRepository,
            CleaningPriceService priceService,
            PhoneNumberNormalizer phoneNumberNormalizer,
            CleaningProperties cleaningProperties,
            CleanerProperties cleanerProperties,
            CustomerAccountService customerAccountService,
            ReferralService referralService,
            Clock clock,
            ApplicationEventPublisher eventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.photoRepository = photoRepository;
        this.orderEventRepository = orderEventRepository;
        this.priceService = priceService;
        this.phoneNumberNormalizer = phoneNumberNormalizer;
        this.cleaningProperties = cleaningProperties;
        this.cleanerProperties = cleanerProperties;
        this.customerAccountService = customerAccountService;
        this.referralService = referralService;
        this.clock = clock;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CleaningOrder createOrder(CreateCleaningOrderCommand command) {
        CurrentCustomer customer = customerAccountService.currentCustomer();
        customerAccountService.lock(customer.id());
        validateRequestedDate(command.requestedDate());
        String normalizedPhone = phoneNumberNormalizer.normalize(command.phone());
        customerAccountService.updatePhone(customer.id(), normalizedPhone);

        var basePrice = priceService.calculate(
                command.apartmentType(),
                command.cleaningType(),
                command.duplex()
        );
        boolean firstOrder = isAcquisitionEligible(customer.id());
        OrderReferralPlan referralPlan = referralService.planForCreation(
                customer.id(),
                command.referralCode(),
                basePrice,
                firstOrder
        );

        var order = new CleaningOrder(
                customer.id(),
                customer.telegramUserId(),
                customer.telegramUsername(),
                customer.displayName(),
                normalizedPhone,
                command.area(),
                command.address().trim(),
                command.apartmentType(),
                command.duplex(),
                command.cleaningType(),
                referralPlan.financialSnapshot(),
                referralPlan.referralCodeId(),
                referralPlan.referrerCustomerId(),
                referralPlan.partnerId(),
                referralPlan.rewardId(),
                cleaningProperties.currency().getCurrencyCode(),
                command.requestedDate(),
                normalizeOptional(command.comment()),
                clock.instant()
        );
        CleaningOrder savedOrder = orderRepository.save(order);
        if (referralPlan.rewardId() != null) {
            referralService.reserveReward(referralPlan, savedOrder.getId());
        }
        recordEvent(
                savedOrder,
                OrderEventType.CREATED,
                null,
                CleaningOrderStatus.NEW,
                OrderActorType.CUSTOMER,
                customer.telegramUserId(),
                null
        );
        eventPublisher.publishEvent(new CleaningOrderCreatedEvent(savedOrder));
        return savedOrder;
    }

    @Transactional
    public CleaningOrderQuoteResponse quoteOrder(CleaningOrderQuoteRequest request) {
        CurrentCustomer customer = customerAccountService.currentCustomer();
        var basePrice = priceService.calculate(
                request.apartmentType(),
                request.cleaningType(),
                request.duplex()
        );
        boolean firstOrder = isAcquisitionEligible(customer.id());
        var plan = referralService.quote(
                customer.id(),
                request.referralCode(),
                basePrice,
                firstOrder
        );
        return CleaningOrderQuoteResponse.from(
                plan.financialSnapshot(),
                cleaningProperties.currency().getCurrencyCode()
        );
    }

    @Transactional
    public List<CleaningOrder> getCurrentCustomerOrders() {
        long customerId = customerAccountService.currentCustomer().id();
        return orderRepository.findAllByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Transactional
    public CleaningOrder getCurrentCustomerOrder(long orderId) {
        long customerId = customerAccountService.currentCustomer().id();
        return findCustomerOrder(orderId, customerId);
    }

    @Transactional
    public CleaningOrder cancelCurrentCustomerOrder(long orderId) {
        CurrentCustomer customer = customerAccountService.currentCustomer();
        long customerId = customer.id();
        var order = findCustomerOrder(orderId, customerId);
        CleaningOrderStatus previousStatus = order.getStatus();
        order.cancelByCustomer();
        referralService.releaseReward(order);
        recordEvent(
                order,
                OrderEventType.CANCELLED_BY_CUSTOMER,
                previousStatus,
                CleaningOrderStatus.CANCELLED,
                OrderActorType.CUSTOMER,
                customer.telegramUserId(),
                null
        );
        return order;
    }

    @Transactional
    public CleaningOrder acceptOrder(long orderId, long cleanerTelegramUserId) {
        requireConfiguredCleaner(cleanerTelegramUserId);
        var acceptedAt = clock.instant();
        int updatedRows = orderRepository.claimNewOrder(
                orderId,
                cleanerTelegramUserId,
                acceptedAt,
                CleaningOrderStatus.NEW,
                CleaningOrderStatus.ACCEPTED
        );
        if (updatedRows != 1) {
            throw new OrderClaimConflictException(orderId);
        }
        CleaningOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        orderEventRepository.save(new CleaningOrderEvent(
                order,
                OrderEventType.ACCEPTED,
                CleaningOrderStatus.NEW,
                CleaningOrderStatus.ACCEPTED,
                OrderActorType.CLEANER,
                cleanerTelegramUserId,
                null,
                acceptedAt
        ));
        return order;
    }

    @Transactional
    public CleaningOrder markAwaitingReport(long orderId, long cleanerTelegramUserId) {
        requireConfiguredCleaner(cleanerTelegramUserId);
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        CleaningOrderStatus previousStatus = order.getStatus();
        order.requireCanStartReport(cleanerTelegramUserId);
        orderRepository.deactivateOtherReportInputs(cleanerTelegramUserId, orderId);
        order.startReportCollection(cleanerTelegramUserId);
        recordEvent(
                order,
                OrderEventType.REPORT_STARTED,
                previousStatus,
                CleaningOrderStatus.AWAITING_REPORT,
                OrderActorType.CLEANER,
                cleanerTelegramUserId,
                null
        );
        return order;
    }

    @Transactional
    public CleaningOrderReportProgress addPhotoToActiveReport(
            long cleanerTelegramUserId,
            String telegramFileId,
            String telegramFileUniqueId,
            String caption
    ) {
        CleaningOrder order = findActiveReport(cleanerTelegramUserId);
        String fileId = requireValue(telegramFileId, 512, "Telegram photo file_id");
        String uniqueId = requireValue(telegramFileUniqueId, 255, "Telegram photo file_unique_id");

        if (!photoRepository.existsByOrderIdAndTelegramFileUniqueId(order.getId(), uniqueId)) {
            photoRepository.save(new CleaningOrderPhoto(order, fileId, uniqueId, clock.instant()));
            recordEvent(
                    order,
                    OrderEventType.PHOTO_ADDED,
                    CleaningOrderStatus.AWAITING_REPORT,
                    CleaningOrderStatus.AWAITING_REPORT,
                    OrderActorType.CLEANER,
                    cleanerTelegramUserId,
                    null
            );
        }
        String normalizedCaption = normalizeCleanerComment(caption);
        if (normalizedCaption != null) {
            order.updateCleanerComment(cleanerTelegramUserId, normalizedCaption);
            recordEvent(
                    order,
                    OrderEventType.COMMENT_UPDATED,
                    CleaningOrderStatus.AWAITING_REPORT,
                    CleaningOrderStatus.AWAITING_REPORT,
                    OrderActorType.CLEANER,
                    cleanerTelegramUserId,
                    null
            );
        }
        return reportProgress(order);
    }

    @Transactional
    public CleaningOrderReportProgress updateActiveReportComment(
            long cleanerTelegramUserId,
            String comment
    ) {
        CleaningOrder order = findActiveReport(cleanerTelegramUserId);
        String normalizedComment = normalizeCleanerComment(comment);
        if (normalizedComment == null) {
            throw new InvalidPhotoReportInputException("Cleaner comment must not be blank");
        }
        order.updateCleanerComment(cleanerTelegramUserId, normalizedComment);
        recordEvent(
                order,
                OrderEventType.COMMENT_UPDATED,
                CleaningOrderStatus.AWAITING_REPORT,
                CleaningOrderStatus.AWAITING_REPORT,
                OrderActorType.CLEANER,
                cleanerTelegramUserId,
                null
        );
        return reportProgress(order);
    }

    @Transactional(readOnly = true)
    public CleaningOrderReport getReportForDelivery(long orderId, long cleanerTelegramUserId) {
        requireConfiguredCleaner(cleanerTelegramUserId);
        CleaningOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.requireReportAccess(cleanerTelegramUserId);
        List<String> fileIds = photoRepository.findAllByOrderIdOrderByCreatedAt(orderId).stream()
                .map(CleaningOrderPhoto::getTelegramFileId)
                .toList();
        if (fileIds.isEmpty()) {
            throw new PhotoReportEmptyException(orderId);
        }
        return new CleaningOrderReport(order, fileIds);
    }

    @Transactional
    public CleaningOrder cancelOrderByCleaner(long orderId, long cleanerTelegramUserId) {
        requireConfiguredCleaner(cleanerTelegramUserId);
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        CleaningOrderStatus previousStatus = order.getStatus();
        order.cancelByCleaner(cleanerTelegramUserId);
        referralService.releaseReward(order);
        recordEvent(
                order,
                OrderEventType.CANCELLED_BY_CLEANER,
                previousStatus,
                CleaningOrderStatus.CANCELLED,
                OrderActorType.CLEANER,
                cleanerTelegramUserId,
                null
        );
        return order;
    }

    @Transactional(readOnly = true)
    public CleaningOrder getOrderForConfiguredCleaner(long orderId, long cleanerTelegramUserId) {
        requireConfiguredCleaner(cleanerTelegramUserId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Transactional
    public CleaningOrder completeOrder(
            long orderId,
            long cleanerTelegramUserId,
            String cleanerComment
    ) {
        requireConfiguredCleaner(cleanerTelegramUserId);
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        CleaningOrderStatus previousStatus = order.getStatus();
        var completedAt = clock.instant();
        order.complete(cleanerTelegramUserId, normalizeCleanerComment(cleanerComment), completedAt);
        orderEventRepository.save(new CleaningOrderEvent(
                order,
                OrderEventType.COMPLETED,
                previousStatus,
                CleaningOrderStatus.COMPLETED,
                OrderActorType.CLEANER,
                cleanerTelegramUserId,
                null,
                completedAt
        ));
        referralService.completeOrder(order);
        return order;
    }

    private void recordEvent(
            CleaningOrder order,
            OrderEventType eventType,
            CleaningOrderStatus fromStatus,
            CleaningOrderStatus toStatus,
            OrderActorType actorType,
            Long actorTelegramUserId,
            String details
    ) {
        orderEventRepository.save(new CleaningOrderEvent(
                order,
                eventType,
                fromStatus,
                toStatus,
                actorType,
                actorTelegramUserId,
                details,
                clock.instant()
        ));
    }

    private CleaningOrder findActiveReport(long cleanerTelegramUserId) {
        requireConfiguredCleaner(cleanerTelegramUserId);
        CleaningOrder order = orderRepository
                .findByCleanerTelegramUserIdAndReportInputActiveTrue(cleanerTelegramUserId)
                .orElseThrow(() -> new ReportCollectionNotActiveException(cleanerTelegramUserId));
        order.requireReportAccess(cleanerTelegramUserId);
        return order;
    }

    private CleaningOrderReportProgress reportProgress(CleaningOrder order) {
        return new CleaningOrderReportProgress(
                order.getId(),
                photoRepository.countByOrderId(order.getId()),
                order.getCleanerComment() != null
        );
    }

    private CleaningOrder findCustomerOrder(long orderId, long customerId) {
        return orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private void validateRequestedDate(LocalDate requestedDate) {
        LocalDate today = LocalDate.now(clock.withZone(cleaningProperties.zoneId()));
        LocalDate latest = today.plusDays(cleaningProperties.bookingDaysAhead());
        if (requestedDate.isBefore(today) || requestedDate.isAfter(latest)) {
            throw new BookingDateNotAvailableException(requestedDate, today, latest);
        }
    }

    private boolean isAcquisitionEligible(long customerId) {
        return !orderRepository.existsByCustomerIdAndStatus(customerId, CleaningOrderStatus.COMPLETED)
                && !orderRepository.existsByCustomerIdAndStatusIn(customerId, ACTIVE_STATUSES);
    }

    private void requireConfiguredCleaner(long cleanerTelegramUserId) {
        if (!cleanerProperties.contains(cleanerTelegramUserId)) {
            throw new CleanerNotAuthorizedException(cleanerTelegramUserId);
        }
    }

    private static String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String normalizeCleanerComment(String value) {
        String normalized = normalizeOptional(value);
        if (normalized != null && normalized.length() > 1000) {
            throw new InvalidPhotoReportInputException("Cleaner comment must not exceed 1000 characters");
        }
        return normalized;
    }

    private static String requireValue(String value, int maxLength, String name) {
        if (value == null || value.isBlank()) {
            throw new InvalidPhotoReportInputException(name + " must not be blank");
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new InvalidPhotoReportInputException(name + " is too long");
        }
        return normalized;
    }
}
