package com.cleany.order;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.configuration.CleanerProperties;
import com.cleany.configuration.CleaningProperties;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.media.ImageMediaTypeDetector;
import com.cleany.media.MediaProvider;
import com.cleany.media.MediaProviderReferenceService;
import com.cleany.media.MediaUpload;
import com.cleany.pricing.CleaningPriceService;
import com.cleany.referral.OrderReferralPlan;
import com.cleany.referral.ReferralUnlockedEvent;
import com.cleany.referral.ReferralService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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
    private final MediaProviderReferenceService mediaProviderReferenceService;
    private final Clock clock;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CleaningOrder createOrder(CreateCleaningOrderCommand command) {
        return createOrder(customerAccountService.currentCustomer(), command);
    }

    @Transactional
    public CleaningOrder createOrder(CurrentCustomer customer, CreateCleaningOrderCommand command) {
        Objects.requireNonNull(customer, "customer");
        customerAccountService.lock(customer.customerId());
        validateRequestedDate(command.requestedDate());
        String normalizedPhone = phoneNumberNormalizer.normalize(command.phone());
        customerAccountService.updatePhone(customer.customerId(), normalizedPhone);

        var basePrice = priceService.calculate(
                command.apartmentType(),
                command.cleaningType(),
                command.duplex()
        );
        boolean firstOrder = isAcquisitionEligible(customer.customerId());
        OrderReferralPlan referralPlan = referralService.planForCreation(
                customer.customerId(),
                command.referralCode(),
                basePrice,
                firstOrder
        );

        var order = new CleaningOrder(
                customer.customerId(),
                customer.externalIdentityId(),
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
                null,
                null
        );
        eventPublisher.publishEvent(new CleaningOrderCreatedEvent(savedOrder));
        return savedOrder;
    }

    @Transactional
    public CleaningOrderQuoteResponse quoteOrder(CleaningOrderQuoteRequest request) {
        return quoteOrder(customerAccountService.currentCustomer(), request);
    }

    @Transactional
    public CleaningOrderQuoteResponse quoteOrder(
            CurrentCustomer customer,
            CleaningOrderQuoteRequest request
    ) {
        Objects.requireNonNull(customer, "customer");
        var basePrice = priceService.calculate(
                request.apartmentType(),
                request.cleaningType(),
                request.duplex()
        );
        boolean firstOrder = isAcquisitionEligible(customer.customerId());
        var plan = referralService.quote(
                customer.customerId(),
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
        long customerId = customerAccountService.currentCustomer().customerId();
        return orderRepository.findAllByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Transactional
    public CleaningOrder getCurrentCustomerOrder(long orderId) {
        long customerId = customerAccountService.currentCustomer().customerId();
        return findCustomerOrder(orderId, customerId);
    }

    @Transactional
    public CleaningOrder cancelCurrentCustomerOrder(long orderId) {
        CurrentCustomer customer = customerAccountService.currentCustomer();
        long customerId = customer.customerId();
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
                null,
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
        eventPublisher.publishEvent(new CleaningOrderCustomerEvent.Accepted(
                order.getId(),
                order.getCustomerId(),
                order.getCommunicationIdentityId()
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
            byte[] content,
            String caption
    ) {
        CleaningOrder order = findActiveReport(cleanerTelegramUserId);
        String fileId = requireValue(telegramFileId, 512, "Telegram photo file_id");
        String uniqueId = requireValue(telegramFileUniqueId, 255, "Telegram photo file_unique_id");
        String contentType = ImageMediaTypeDetector.detect(content)
                .orElseThrow(() -> new InvalidCompletionPhotoException(
                        "Completion report photo must be JPEG or PNG"
                ));
        var providerMedia = mediaProviderReferenceService.resolveOrStore(
                new MediaUpload(content, contentType),
                MediaProvider.TELEGRAM,
                fileId,
                uniqueId
        );

        if (!photoRepository.existsByOrderIdAndMediaAssetId(
                order.getId(),
                providerMedia.media().mediaId()
        )) {
            photoRepository.save(new CleaningOrderPhoto(
                    order,
                    providerMedia.media().mediaId(),
                    clock.instant()
            ));
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
        List<Long> mediaIds = photoRepository.findAllByOrderIdOrderByCreatedAt(orderId).stream()
                .map(CleaningOrderPhoto::getMediaAssetId)
                .toList();
        if (mediaIds.isEmpty()) {
            throw new PhotoReportEmptyException(orderId);
        }
        return new CleaningOrderReport(order, mediaIds);
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
        eventPublisher.publishEvent(new CleaningOrderCustomerEvent.Cancelled(
                order.getId(),
                order.getCustomerId(),
                order.getCommunicationIdentityId()
        ));
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
        order.requireReportAccess(cleanerTelegramUserId);
        boolean firstCompletedOrder = !orderRepository.existsByCustomerIdAndStatus(
                order.getCustomerId(),
                CleaningOrderStatus.COMPLETED
        );
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
        String referralCode = referralService.completeOrder(order);
        eventPublisher.publishEvent(new CleaningOrderCustomerEvent.Completed(
                order.getId(),
                order.getCustomerId(),
                order.getCommunicationIdentityId()
        ));
        if (firstCompletedOrder) {
            eventPublisher.publishEvent(new ReferralUnlockedEvent(
                    order.getCustomerId(),
                    order.getCommunicationIdentityId(),
                    referralCode
            ));
        }
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
