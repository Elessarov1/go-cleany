package com.cleany.notification;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.customer.CustomerExternalIdentity;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;

class CustomerNotificationDispatcherTest {

    @Test
    void telegramWriteAccessAllowed_recordsAndDeliversOnce() {
        var repository = Mockito.mock(CustomerExternalIdentityRepository.class);
        var recorder = Mockito.mock(CustomerNotificationRecorder.class);
        var sender = Mockito.mock(CustomerNotificationSender.class);
        var identity = telegramIdentity(true);
        var notification = new ReferralUnlockedCustomerNotification("ALEX7K2");
        Mockito.when(recorder.record(77L, notification)).thenReturn(true);
        Mockito.when(sender.provider()).thenReturn(ExternalIdentityProvider.TELEGRAM);
        Mockito.when(repository.findByIdAndCustomerId(88L, 77L)).thenReturn(Optional.of(identity));
        Mockito.when(repository.findAllByCustomerIdOrderByProvider(77L)).thenReturn(List.of(identity));

        boolean delivered = new CustomerNotificationDispatcher(repository, recorder, List.of(sender))
                .send(77L, 88L, notification);

        Assertions.assertTrue(delivered);
        Mockito.verify(sender).send(Mockito.any(CommunicationTarget.class), Mockito.eq(notification));
    }

    @Test
    void googleOnlyAndTelegramWithoutWriteAccess_remainPersistedWithoutExternalDelivery() {
        var repository = Mockito.mock(CustomerExternalIdentityRepository.class);
        var recorder = Mockito.mock(CustomerNotificationRecorder.class);
        var sender = Mockito.mock(CustomerNotificationSender.class);
        var google = Mockito.mock(CustomerExternalIdentity.class);
        var telegram = telegramIdentity(false);
        var notification = new ReferralUnlockedCustomerNotification("ALEX7K2");
        Mockito.when(recorder.record(77L, notification)).thenReturn(true);
        Mockito.when(sender.provider()).thenReturn(ExternalIdentityProvider.TELEGRAM);
        Mockito.when(repository.findByIdAndCustomerId(88L, 77L)).thenReturn(Optional.of(google));
        Mockito.when(repository.findAllByCustomerIdOrderByProvider(77L)).thenReturn(List.of(google, telegram));
        Mockito.when(google.getProvider()).thenReturn(ExternalIdentityProvider.GOOGLE);

        boolean delivered = new CustomerNotificationDispatcher(repository, recorder, List.of(sender))
                .send(77L, 88L, notification);

        Assertions.assertFalse(delivered);
        Mockito.verify(sender, Mockito.never()).send(Mockito.any(), Mockito.any());
    }

    @Test
    void senderFailure_doesNotUndoPersistentNotification() {
        var repository = Mockito.mock(CustomerExternalIdentityRepository.class);
        var recorder = Mockito.mock(CustomerNotificationRecorder.class);
        var sender = Mockito.mock(CustomerNotificationSender.class);
        var identity = telegramIdentity(true);
        var notification = new ReferralUnlockedCustomerNotification("ALEX7K2");
        Mockito.when(recorder.record(77L, notification)).thenReturn(true);
        Mockito.when(sender.provider()).thenReturn(ExternalIdentityProvider.TELEGRAM);
        Mockito.when(repository.findAllByCustomerIdOrderByProvider(77L)).thenReturn(List.of(identity));
        Mockito.doThrow(new IllegalStateException("Telegram unavailable"))
                .when(sender).send(Mockito.any(), Mockito.eq(notification));

        boolean delivered = new CustomerNotificationDispatcher(repository, recorder, List.of(sender))
                .send(77L, 88L, notification);

        Assertions.assertFalse(delivered);
        Mockito.verify(recorder).record(77L, notification);
    }

    @Test
    void duplicateNotification_isCompleteNoOp() {
        var repository = Mockito.mock(CustomerExternalIdentityRepository.class);
        var recorder = Mockito.mock(CustomerNotificationRecorder.class);
        var sender = Mockito.mock(CustomerNotificationSender.class);
        var notification = new ReferralUnlockedCustomerNotification("ALEX7K2");
        Mockito.when(recorder.record(77L, notification)).thenReturn(false);
        Mockito.when(sender.provider()).thenReturn(ExternalIdentityProvider.TELEGRAM);

        boolean delivered = new CustomerNotificationDispatcher(repository, recorder, List.of(sender))
                .send(77L, 88L, notification);

        Assertions.assertFalse(delivered);
        Mockito.verifyNoInteractions(repository);
        Mockito.verify(sender, Mockito.never()).send(Mockito.any(), Mockito.any());
    }

    @Test
    void duplicateProviderSenders_configurationRejected() {
        CustomerNotificationSender first = Mockito.mock(CustomerNotificationSender.class);
        CustomerNotificationSender second = Mockito.mock(CustomerNotificationSender.class);
        Mockito.when(first.provider()).thenReturn(ExternalIdentityProvider.TELEGRAM);
        Mockito.when(second.provider()).thenReturn(ExternalIdentityProvider.TELEGRAM);

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> new CustomerNotificationDispatcher(
                        Mockito.mock(CustomerExternalIdentityRepository.class),
                        Mockito.mock(CustomerNotificationRecorder.class),
                        List.of(first, second)
                )
        );
    }

    private static CustomerExternalIdentity telegramIdentity(boolean writeAccessAllowed) {
        var identity = Mockito.mock(CustomerExternalIdentity.class);
        Mockito.when(identity.getCustomerId()).thenReturn(77L);
        Mockito.when(identity.getId()).thenReturn(88L);
        Mockito.when(identity.getProvider()).thenReturn(ExternalIdentityProvider.TELEGRAM);
        Mockito.when(identity.getExternalSubject()).thenReturn("900001");
        Mockito.when(identity.getLanguageCode()).thenReturn("ru");
        Mockito.when(identity.isWriteAccessAllowed()).thenReturn(writeAccessAllowed);
        return identity;
    }
}
