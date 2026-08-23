package com.cleany.rental;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

class RentalBookingAdminNotificationListenerTest {

    private final RentalBookingAdminNotificationQueryService queryService =
            Mockito.mock(RentalBookingAdminNotificationQueryService.class);
    private final RentalAdminNotificationSender sender =
            Mockito.mock(RentalAdminNotificationSender.class);
    private final RentalBookingAdminNotificationListener listener =
            new RentalBookingAdminNotificationListener(queryService, List.of(sender));

    @Test
    void listener_runsOnlyAfterSuccessfulCommit() throws NoSuchMethodException {
        var method = RentalBookingAdminNotificationListener.class.getDeclaredMethod(
                "notifyAdmins",
                RentalBookingAdminEvent.class
        );
        var annotation = method.getAnnotation(TransactionalEventListener.class);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(annotation),
                () -> Assertions.assertEquals(TransactionPhase.AFTER_COMMIT, annotation.phase()),
                () -> Assertions.assertFalse(annotation.fallbackExecution())
        );
    }

    @Test
    void committedEvent_isResolvedAndSentThroughNeutralBoundary() {
        var event = new RentalBookingAdminEvent(
                42L,
                RentalBookingAdminEvent.Type.CREATED
        );
        RentalBookingAdminNotification notification = notification(RentalTermType.DATE_RANGE);
        Mockito.when(queryService.get(42L)).thenReturn(notification);

        listener.notifyAdmins(event);

        Mockito.verify(sender).send(RentalBookingAdminEvent.Type.CREATED, notification);
    }

    @Test
    void deliveryFailure_doesNotEscapeAfterCommitListener() {
        var event = new RentalBookingAdminEvent(
                42L,
                RentalBookingAdminEvent.Type.CANCELLED_BY_CUSTOMER
        );
        RentalBookingAdminNotification notification = notification(RentalTermType.DATE_RANGE);
        Mockito.when(queryService.get(42L)).thenReturn(notification);
        Mockito.doThrow(new IllegalStateException("channel unavailable"))
                .when(sender)
                .send(event.type(), notification);

        Assertions.assertDoesNotThrow(() -> listener.notifyAdmins(event));
    }

    private static RentalBookingAdminNotification notification(RentalTermType termType) {
        return new RentalBookingAdminNotification(
                42L,
                "Sea View 1+1",
                "Alexandr",
                "+90 555 123 45 67",
                termType,
                LocalDate.of(2026, 9, 15),
                LocalDate.of(2026, 9, 29),
                null,
                14,
                new BigDecimal("2000.00"),
                null,
                new BigDecimal("28000.00"),
                "TRY"
        );
    }
}
