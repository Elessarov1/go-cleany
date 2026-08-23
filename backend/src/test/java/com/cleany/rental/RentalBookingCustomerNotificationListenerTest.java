package com.cleany.rental;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.notification.CustomerNotificationDispatcher;

class RentalBookingCustomerNotificationListenerTest {

    private final CustomerNotificationDispatcher dispatcher =
            Mockito.mock(CustomerNotificationDispatcher.class);
    private final RentalBookingNotificationQueryService queryService =
            Mockito.mock(RentalBookingNotificationQueryService.class);
    private final RentalBookingCustomerNotificationListener listener =
            new RentalBookingCustomerNotificationListener(dispatcher, queryService);

    @Test
    void listener_runsOnlyAfterSuccessfulCommit() throws NoSuchMethodException {
        var method = RentalBookingCustomerNotificationListener.class.getDeclaredMethod(
                "notifyCustomer",
                RentalBookingCustomerEvent.class
        );
        var annotation = method.getAnnotation(TransactionalEventListener.class);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(annotation),
                () -> Assertions.assertEquals(TransactionPhase.AFTER_COMMIT, annotation.phase()),
                () -> Assertions.assertFalse(annotation.fallbackExecution())
        );
    }

    @Test
    void confirmedBooking_dispatchedThroughRecordedCommunicationIdentity() {
        var event = new RentalBookingCustomerEvent.Confirmed(43L, 77L, 88L);
        var notification = new RentalBookingCustomerNotification.Confirmed(
                43L,
                "Квартира",
                "Apartment",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 8),
                new BigDecimal("700.00"),
                "TRY"
        );
        Mockito.when(queryService.confirmed(43L)).thenReturn(notification);

        listener.notifyCustomer(event);

        Mockito.verify(dispatcher).send(77L, 88L, notification);
    }

    @Test
    void deliveryFailure_doesNotEscapeAfterCommitListener() {
        var event = new RentalBookingCustomerEvent.Cancelled(
                43L,
                77L,
                88L,
                RentalBookingStatus.CANCELLED_BY_ADMIN
        );
        var notification = new RentalBookingCustomerNotification.Cancelled(
                43L,
                "Квартира",
                "Apartment",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 8),
                RentalBookingStatus.CANCELLED_BY_ADMIN
        );
        Mockito.when(queryService.cancelled(43L)).thenReturn(notification);
        Mockito.when(dispatcher.send(77L, 88L, notification))
                .thenThrow(new IllegalStateException("channel unavailable"));

        Assertions.assertDoesNotThrow(() -> listener.notifyCustomer(event));
    }
}
