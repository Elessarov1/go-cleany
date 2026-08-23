package com.cleany.rental;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RentalBookingAdminNotificationListener {

    private final RentalBookingAdminNotificationQueryService queryService;
    private final List<RentalAdminNotificationSender> senders;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyAdmins(RentalBookingAdminEvent event) {
        if (senders.isEmpty()) {
            return;
        }
        try {
            RentalBookingAdminNotification notification = queryService.get(event.bookingId());
            senders.forEach(sender -> send(sender, event, notification));
        } catch (RuntimeException exception) {
            log.error(
                    "Rental admin notification preparation failed for booking {}",
                    event.bookingId(),
                    exception
            );
        }
    }

    private static void send(
            RentalAdminNotificationSender sender,
            RentalBookingAdminEvent event,
            RentalBookingAdminNotification notification
    ) {
        try {
            sender.send(event.type(), notification);
        } catch (RuntimeException exception) {
            log.error(
                    "Rental admin notification delivery failed for booking {} via {}",
                    event.bookingId(),
                    sender.getClass().getSimpleName(),
                    exception
            );
        }
    }
}
