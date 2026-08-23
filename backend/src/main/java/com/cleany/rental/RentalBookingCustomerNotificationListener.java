package com.cleany.rental;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.notification.CustomerNotification;
import com.cleany.notification.CustomerNotificationDispatcher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RentalBookingCustomerNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(
            RentalBookingCustomerNotificationListener.class
    );

    private final CustomerNotificationDispatcher dispatcher;
    private final RentalBookingNotificationQueryService queryService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyCustomer(RentalBookingCustomerEvent event) {
        try {
            CustomerNotification notification = switch (event) {
                case RentalBookingCustomerEvent.Confirmed confirmed ->
                        queryService.confirmed(confirmed.bookingId());
                case RentalBookingCustomerEvent.Cancelled cancelled ->
                        queryService.cancelled(cancelled.bookingId());
            };
            dispatcher.send(
                    event.customerId(),
                    event.communicationIdentityId(),
                    notification
            );
        } catch (RuntimeException exception) {
            log.error(
                    "Rental booking customer notification failed for booking {} and communication identity {}",
                    event.bookingId(),
                    event.communicationIdentityId(),
                    exception
            );
        }
    }
}
