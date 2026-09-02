package com.cleany.reminder;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.catalog.PlatformService;
import com.cleany.catalog.PlatformServiceAccessService;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextAvailability;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextService;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextType;
import com.cleany.notification.CustomerNotificationDispatcher;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderStatus;
import com.cleany.rental.RentalBooking;
import com.cleany.rental.RentalBookingRepository;
import com.cleany.transfer.TransferBooking;
import com.cleany.transfer.TransferBookingPolicy;
import com.cleany.transfer.TransferBookingRepository;
import com.cleany.transfer.TransferBookingStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmartReminderService {

    private final CustomerReminderRepository reminderRepository;
    private final JdbcTemplate jdbcTemplate;
    private final CleaningOrderRepository cleaningOrderRepository;
    private final RentalBookingRepository rentalBookingRepository;
    private final TransferBookingRepository transferBookingRepository;
    private final CleaningRepeatReminderService cleaningReminderService;
    private final RentalTransferContextService rentalTransferContextService;
    private final PlatformServiceAccessService serviceAccessService;
    private final CustomerNotificationDispatcher notificationDispatcher;
    private final TransferBookingPolicy transferBookingPolicy;
    private final SmartReminderProperties properties;
    private final Clock clock;

    @Transactional
    public SmartReminderProcessingResult process() {
        LocalDate today = LocalDate.now(clock.withZone(properties.zoneId()));
        var counters = new Counters();
        processCleaning(today, counters);
        processRentalCheckout(today, counters);
        processUpcomingTransfers(today, counters);
        return new SmartReminderProcessingResult(
                counters.notified,
                counters.superseded,
                counters.expired
        );
    }

    private void processCleaning(LocalDate today, Counters counters) {
        var due = reminderRepository
                .findAllByTypeAndStatusAndScheduledDateLessThanEqualOrderByScheduledDateAscIdAsc(
                        CustomerReminderType.CLEANING_REPEAT,
                        CustomerReminderStatus.PENDING,
                        today,
                        PageRequest.of(0, properties.batchSize())
                );
        for (CustomerReminder reminder : due) {
            CleaningOrder order = cleaningOrderRepository.findById(reminder.getSourceEntityId())
                    .orElse(null);
            if (order == null || order.getStatus() != CleaningOrderStatus.COMPLETED) {
                reminder.markExpired(clock.instant());
                counters.expired++;
                continue;
            }
            if (cleaningReminderService.hasLaterMatchingOrder(order)) {
                reminder.markSuperseded(clock.instant());
                counters.superseded++;
                continue;
            }
            if (today.isAfter(reminder.getScheduledDate().plusDays(properties.cleaningGraceDays()))) {
                reminder.markExpired(clock.instant());
                counters.expired++;
                continue;
            }
            if (!serviceAccessService.canStartCustomerFlow(
                    PlatformService.CLEANING,
                    reminder.getCustomerId()
            )) {
                continue;
            }
            notify(
                    reminder,
                    order.getCommunicationIdentityId(),
                    new ReminderCustomerNotification.CleaningRepeat(
                            order.getId(),
                            reminder.getScheduledDate()
                    )
            );
            counters.notified++;
        }
    }

    private void processRentalCheckout(LocalDate today, Counters counters) {
        LocalDate windowEnd = today.plusDays(properties.rentalTransferDaysBefore());
        for (long bookingId : rentalBookingRepository.findCheckoutReminderCandidates(
                today,
                windowEnd,
                properties.batchSize()
        )) {
            RentalBooking booking = rentalBookingRepository.findById(bookingId).orElse(null);
            if (booking == null || booking.getCheckOutDate()
                    .minusDays(properties.rentalTransferDaysBefore())
                    .isAfter(today)) {
                continue;
            }
            boolean bookable = rentalTransferContextService
                    .contextForCustomer(booking.getCustomerId(), booking.getId())
                    .options()
                    .stream()
                    .anyMatch(option -> option.context() == RentalTransferContextType.CHECKOUT
                            && option.availability() == RentalTransferContextAvailability.BOOKABLE);
            if (!bookable) {
                continue;
            }
            CustomerReminder reminder = ensureAutomatic(
                    booking.getCustomerId(),
                    CustomerReminderType.RENTAL_CHECKOUT_TRANSFER,
                    booking.getId(),
                    booking.getCheckOutDate().minusDays(properties.rentalTransferDaysBefore())
            );
            if (reminder.getStatus() != CustomerReminderStatus.PENDING) {
                continue;
            }
            notify(
                    reminder,
                    booking.getCommunicationIdentityId(),
                    new ReminderCustomerNotification.RentalCheckoutTransfer(
                            booking.getId(),
                            booking.getCheckOutDate()
                    )
            );
            counters.notified++;
        }
    }

    private void processUpcomingTransfers(LocalDate today, Counters counters) {
        var candidates = transferBookingRepository
                .findAllByStatusAndPickupDateBetweenOrderByPickupDateAscPickupTimeAscIdAsc(
                        TransferBookingStatus.CONFIRMED,
                        today,
                        today.plusDays(properties.transferDaysBefore()),
                        PageRequest.of(0, properties.batchSize())
                );
        for (TransferBooking booking : candidates) {
            if (!clock.instant().isBefore(transferBookingPolicy.pickupInstant(
                    booking.getPickupDate(),
                    booking.getPickupTime()
            ))) {
                continue;
            }
            LocalDate scheduledDate = booking.getPickupDate().minusDays(properties.transferDaysBefore());
            if (scheduledDate.isAfter(today)) {
                continue;
            }
            CustomerReminder reminder = ensureAutomatic(
                    booking.getCustomerId(),
                    CustomerReminderType.TRANSFER_UPCOMING,
                    booking.getId(),
                    scheduledDate
            );
            if (reminder.getStatus() != CustomerReminderStatus.PENDING) {
                continue;
            }
            notify(
                    reminder,
                    booking.getCommunicationIdentityId(),
                    new ReminderCustomerNotification.TransferUpcoming(
                            booking.getId(),
                            booking.getPickupDate(),
                            booking.getPickupTime(),
                            booking.getDirection(),
                            booking.getAirportCodeSnapshot()
                    )
            );
            counters.notified++;
        }
    }

    private CustomerReminder ensureAutomatic(
            long customerId,
            CustomerReminderType type,
            long sourceEntityId,
            LocalDate scheduledDate
    ) {
        return reminderRepository.findByCustomerIdAndTypeAndSourceServiceAndSourceEntityId(
                        customerId,
                        type,
                        type.sourceService(),
                        sourceEntityId
                )
                .orElseGet(() -> createAutomatic(
                        customerId,
                        type,
                        sourceEntityId,
                        scheduledDate
                ));
    }

    private CustomerReminder createAutomatic(
            long customerId,
            CustomerReminderType type,
            long sourceEntityId,
            LocalDate scheduledDate
    ) {
        var now = clock.instant();
        jdbcTemplate.update("""
                insert into customer_reminder (
                    customer_id, type, source_service, source_entity_id,
                    scheduled_date, cleaning_interval_days, status, created_at, updated_at
                ) values (?, ?, ?, ?, ?, null, 'PENDING', ?, ?)
                on conflict (customer_id, type, source_service, source_entity_id) do nothing
                """,
                customerId,
                type.name(),
                type.sourceService().name(),
                sourceEntityId,
                scheduledDate,
                now.atOffset(ZoneOffset.UTC),
                now.atOffset(ZoneOffset.UTC)
        );
        return reminderRepository.findByCustomerIdAndTypeAndSourceServiceAndSourceEntityId(
                        customerId,
                        type,
                        type.sourceService(),
                        sourceEntityId
                )
                .orElseThrow(() -> new IllegalStateException(
                        "Automatic reminder was not persisted for " + type + ":" + sourceEntityId
                ));
    }

    private void notify(
            CustomerReminder reminder,
            long communicationIdentityId,
            ReminderCustomerNotification notification
    ) {
        Objects.requireNonNull(notification, "notification");
        notificationDispatcher.send(
                reminder.getCustomerId(),
                communicationIdentityId,
                notification
        );
        reminder.markNotified(clock.instant());
    }

    private static final class Counters {
        private int notified;
        private int superseded;
        private int expired;
    }
}
