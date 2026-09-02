package com.cleany.reminder;

import java.time.Clock;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.catalog.PlatformService;
import com.cleany.common.text.AddressNormalizer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderStatus;
import com.cleany.order.OrderNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CleaningRepeatReminderService {

    private static final Set<CleaningOrderStatus> NON_SUPERSEDING_STATUSES = EnumSet.of(
            CleaningOrderStatus.CANCELLED,
            CleaningOrderStatus.REJECTED
    );

    private final CleaningOrderRepository orderRepository;
    private final CustomerReminderRepository reminderRepository;
    private final CustomerAccountService customerAccountService;
    private final SmartReminderProperties properties;
    private final Clock clock;

    @Transactional(readOnly = true)
    public CleaningRepeatReminderResponse current(long orderId) {
        CleaningOrder order = requireCompletedOwnedOrder(orderId);
        return find(order)
                .map(CleaningRepeatReminderResponse::from)
                .orElseGet(CleaningRepeatReminderResponse::notConfigured);
    }

    @Transactional
    public CleaningRepeatReminderResponse update(
            long orderId,
            CleaningRepeatReminderSelection selection
    ) {
        CleaningOrder order = requireCompletedOwnedOrder(orderId);
        CleaningRepeatReminderSelection requiredSelection = Objects.requireNonNull(
                selection,
                "selection"
        );
        var schedule = schedule(order, requiredSelection);
        var status = requiredSelection == CleaningRepeatReminderSelection.DO_NOT_REMIND
                ? CustomerReminderStatus.DISABLED
                : CustomerReminderStatus.PENDING;
        var now = clock.instant();
        CustomerReminder reminder = find(order).orElseGet(() -> new CustomerReminder(
                order.getCustomerId(),
                CustomerReminderType.CLEANING_REPEAT,
                order.getId(),
                schedule,
                requiredSelection.intervalDays(),
                status,
                now
        ));
        if (reminder.getId() != null) {
            reminder.configureCleaning(
                    schedule,
                    requiredSelection.intervalDays(),
                    status,
                    now
            );
        }
        if (status == CustomerReminderStatus.PENDING && hasLaterMatchingOrder(order)) {
            reminder.markSuperseded(now);
        }
        return CleaningRepeatReminderResponse.from(reminderRepository.save(reminder));
    }

    public boolean hasLaterMatchingOrder(CleaningOrder source) {
        String normalizedAddress = AddressNormalizer.normalize(source.getAddress());
        return orderRepository.findAllByCustomerIdOrderByCreatedAtDesc(source.getCustomerId()).stream()
                .filter(candidate -> !candidate.getId().equals(source.getId()))
                .filter(candidate -> candidate.getId() > source.getId())
                .filter(candidate -> !NON_SUPERSEDING_STATUSES.contains(candidate.getStatus()))
                .filter(candidate -> candidate.getArea() == source.getArea())
                .map(CleaningOrder::getAddress)
                .map(AddressNormalizer::normalize)
                .anyMatch(normalizedAddress::equals);
    }

    private java.util.Optional<CustomerReminder> find(CleaningOrder order) {
        return reminderRepository.findByCustomerIdAndTypeAndSourceServiceAndSourceEntityId(
                order.getCustomerId(),
                CustomerReminderType.CLEANING_REPEAT,
                PlatformService.CLEANING,
                order.getId()
        );
    }

    private CleaningOrder requireCompletedOwnedOrder(long orderId) {
        long customerId = customerAccountService.currentCustomer().customerId();
        CleaningOrder order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (order.getStatus() != CleaningOrderStatus.COMPLETED || order.getCompletedAt() == null) {
            throw new ReminderSourceNotEligibleException(orderId);
        }
        return order;
    }

    private LocalDate schedule(
            CleaningOrder order,
            CleaningRepeatReminderSelection selection
    ) {
        if (selection.intervalDays() == null) {
            return null;
        }
        return order.getCompletedAt()
                .atZone(properties.zoneId())
                .toLocalDate()
                .plusDays(selection.intervalDays());
    }
}
