package com.cleany.notification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.referral.ReferralUnlockedEvent;

class ReferralUnlockedNotificationListenerTest {

    @Test
    void listener_runsOnlyAfterSuccessfulCommit() throws NoSuchMethodException {
        var method = ReferralUnlockedNotificationListener.class.getDeclaredMethod(
                "notifyCustomer",
                ReferralUnlockedEvent.class
        );
        var annotation = method.getAnnotation(TransactionalEventListener.class);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(annotation),
                () -> Assertions.assertEquals(TransactionPhase.AFTER_COMMIT, annotation.phase()),
                () -> Assertions.assertFalse(annotation.fallbackExecution())
        );
    }

    @Test
    void dispatcherFailure_doesNotEscapeAfterCommitListener() {
        CustomerNotificationDispatcher dispatcher = Mockito.mock(CustomerNotificationDispatcher.class);
        Mockito.when(dispatcher.send(
                        Mockito.eq(77L),
                        Mockito.eq(88L),
                        Mockito.any(ReferralUnlockedCustomerNotification.class)
                )).thenThrow(new IllegalStateException("Telegram unavailable"));
        var listener = new ReferralUnlockedNotificationListener(dispatcher);

        Assertions.assertDoesNotThrow(
                () -> listener.notifyCustomer(new ReferralUnlockedEvent(77L, 88L, "ALEX7K2"))
        );

        Mockito.verify(dispatcher).send(
                77L,
                88L,
                new ReferralUnlockedCustomerNotification("ALEX7K2")
        );
    }
}
