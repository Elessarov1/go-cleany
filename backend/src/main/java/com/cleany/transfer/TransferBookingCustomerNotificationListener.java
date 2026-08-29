package com.cleany.transfer;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.notification.CustomerNotificationDispatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferBookingCustomerNotificationListener {

    private final CustomerNotificationDispatcher dispatcher;
    private final TransferBookingNotificationQueryService queryService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void requested(TransferBookingCreatedEvent event) {
        send(event.booking().id(), event.customerId(), event.communicationIdentityId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void statusChanged(TransferBookingCustomerEvent event) {
        send(event.bookingId(), event.customerId(), event.communicationIdentityId());
    }

    private void send(
            long bookingId,
            long customerId,
            long communicationIdentityId
    ) {
        try {
            dispatcher.send(
                    customerId,
                    communicationIdentityId,
                    queryService.customer(bookingId)
            );
        } catch (RuntimeException exception) {
            log.error(
                    "Transfer customer status notification failed for booking {}",
                    bookingId,
                    exception
            );
        }
    }
}
