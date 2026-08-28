package com.cleany.crossservice.rentalcleaning;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.notification.CustomerNotificationDispatcher;
import com.cleany.catalog.PlatformService;
import com.cleany.catalog.PlatformServiceAccessService;

class RentalCleaningBenefitNotificationListenerTest {

    private final CustomerNotificationDispatcher dispatcher =
            Mockito.mock(CustomerNotificationDispatcher.class);
    private final RentalCleaningBenefitNotificationQueryService queryService =
            Mockito.mock(RentalCleaningBenefitNotificationQueryService.class);
    private final PlatformServiceAccessService serviceAccessService =
            Mockito.mock(PlatformServiceAccessService.class);
    private final RentalCleaningBenefitNotificationListener listener =
            new RentalCleaningBenefitNotificationListener(
                    dispatcher,
                    queryService,
                    serviceAccessService
            );

    @Test
    void listenerRunsOnlyAfterSuccessfulCommit() throws NoSuchMethodException {
        var method = RentalCleaningBenefitNotificationListener.class.getDeclaredMethod(
                "notifyCustomer",
                RentalCleaningBenefitIssuedEvent.class
        );
        var annotation = method.getAnnotation(TransactionalEventListener.class);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(annotation),
                () -> Assertions.assertEquals(TransactionPhase.AFTER_COMMIT, annotation.phase()),
                () -> Assertions.assertFalse(annotation.fallbackExecution())
        );
    }

    @Test
    void issuedBenefitDispatchedThroughRecordedCommunicationIdentity() {
        var event = new RentalCleaningBenefitIssuedEvent(11L, 42L, 77L, 88L);
        var notification = new RentalCleaningBenefitCustomerNotification(
                999L,
                42L,
                "RC23456789",
                LocalDate.of(2026, 9, 12),
                LocalDate.of(2026, 9, 15)
        );
        Mockito.when(queryService.issued(11L)).thenReturn(notification);
        Mockito.when(serviceAccessService.canStartCustomerFlow(
                PlatformService.CLEANING,
                77L
        )).thenReturn(true);

        listener.notifyCustomer(event);

        Mockito.verify(dispatcher).send(77L, 88L, notification);
    }

    @Test
    void deliveryFailureDoesNotEscapeAfterCommitListener() {
        var event = new RentalCleaningBenefitIssuedEvent(11L, 42L, 77L, 88L);
        var notification = new RentalCleaningBenefitCustomerNotification(
                999L,
                42L,
                "RC23456789",
                LocalDate.of(2026, 9, 12),
                LocalDate.of(2026, 9, 15)
        );
        Mockito.when(queryService.issued(11L)).thenReturn(notification);
        Mockito.when(serviceAccessService.canStartCustomerFlow(
                PlatformService.CLEANING,
                77L
        )).thenReturn(true);
        Mockito.when(dispatcher.send(77L, 88L, notification))
                .thenThrow(new IllegalStateException("channel unavailable"));

        Assertions.assertDoesNotThrow(() -> listener.notifyCustomer(event));
    }

    @Test
    void unavailableCleaningSkipsNotification() {
        var event = new RentalCleaningBenefitIssuedEvent(11L, 42L, 77L, 88L);
        Mockito.when(serviceAccessService.canStartCustomerFlow(
                PlatformService.CLEANING,
                77L
        )).thenReturn(false);

        listener.notifyCustomer(event);

        Mockito.verifyNoInteractions(queryService, dispatcher);
    }
}
