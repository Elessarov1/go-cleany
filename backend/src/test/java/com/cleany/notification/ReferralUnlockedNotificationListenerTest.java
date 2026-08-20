package com.cleany.notification;

import java.util.List;

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
    void failedChannel_doesNotPreventOtherChannelsFromReceivingNotification() {
        CustomerNotificationSender failedSender = Mockito.mock(CustomerNotificationSender.class);
        CustomerNotificationSender successfulSender = Mockito.mock(CustomerNotificationSender.class);
        Mockito.doThrow(new IllegalStateException("Telegram unavailable"))
                .when(failedSender)
                .sendReferralUnlocked(77L, "ALEX7K2");
        var listener = new ReferralUnlockedNotificationListener(List.of(failedSender, successfulSender));

        listener.notifyCustomer(new ReferralUnlockedEvent(77L, "ALEX7K2"));

        Mockito.verify(failedSender).sendReferralUnlocked(77L, "ALEX7K2");
        Mockito.verify(successfulSender).sendReferralUnlocked(77L, "ALEX7K2");
    }
}
