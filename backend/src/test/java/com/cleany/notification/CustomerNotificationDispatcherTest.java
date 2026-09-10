package com.cleany.notification;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.communication.CommunicationEndpoint;
import com.cleany.communication.CommunicationEndpointService;
import com.cleany.communication.NotificationDeliveryQueue;
import com.cleany.communication.NotificationPreferenceService;
import com.cleany.communication.NotificationPreferencesResponse;
import com.cleany.customer.CustomerExternalIdentity;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;

class CustomerNotificationDispatcherTest {

    @Test
    void telegramWriteAccessAllowed_recordsInboxAndDurableDeliveryTogether() {
        Fixture fixture = fixture();
        var identity = telegramIdentity(true);
        var endpoint = Mockito.mock(CommunicationEndpoint.class);
        Mockito.when(fixture.recorder.recordId(77L, fixture.notification)).thenReturn(101L);
        Mockito.when(fixture.repository.findAllByCustomerIdOrderByProvider(77L)).thenReturn(List.of(identity));
        Mockito.when(fixture.preferences.forCustomer(77L))
                .thenReturn(new NotificationPreferencesResponse(true, true));
        Mockito.when(fixture.endpoints.ensureTelegram(identity)).thenReturn(endpoint);
        Mockito.when(fixture.endpoints.active(77L)).thenReturn(Collections.emptyList());
        Mockito.when(fixture.queue.enqueue(101L, List.of(endpoint))).thenReturn(1);

        Assertions.assertTrue(fixture.dispatcher().send(77L, 88L, fixture.notification));
        Mockito.verify(fixture.queue).enqueue(101L, List.of(endpoint));
    }

    @Test
    void noEnabledEndpoint_stillPersistsInboxWithoutDeliveryJob() {
        Fixture fixture = fixture();
        CustomerExternalIdentity identity = telegramIdentity(false);
        Mockito.when(fixture.recorder.recordId(77L, fixture.notification)).thenReturn(101L);
        Mockito.when(fixture.repository.findAllByCustomerIdOrderByProvider(77L))
                .thenReturn(List.of(identity));
        Mockito.when(fixture.preferences.forCustomer(77L))
                .thenReturn(new NotificationPreferencesResponse(true, true));
        Mockito.when(fixture.endpoints.active(77L)).thenReturn(Collections.emptyList());

        Assertions.assertFalse(fixture.dispatcher().send(77L, 88L, fixture.notification));
        Mockito.verify(fixture.queue).enqueue(101L, Collections.emptyList());
    }

    @Test
    void duplicateNotification_isCompleteNoOp() {
        Fixture fixture = fixture();
        Mockito.when(fixture.recorder.recordId(77L, fixture.notification)).thenReturn(null);

        Assertions.assertFalse(fixture.dispatcher().send(77L, 88L, fixture.notification));
        Mockito.verifyNoInteractions(fixture.repository, fixture.endpoints, fixture.preferences, fixture.queue);
    }

    @Test
    void legacySendAfterCommitName_nowEnqueuesInsideCallingTransaction() {
        Fixture fixture = fixture();
        Mockito.when(fixture.recorder.recordId(77L, fixture.notification)).thenReturn(101L);
        Mockito.when(fixture.repository.findAllByCustomerIdOrderByProvider(77L))
                .thenReturn(Collections.emptyList());
        Mockito.when(fixture.preferences.forCustomer(77L))
                .thenReturn(new NotificationPreferencesResponse(false, false));

        fixture.dispatcher().sendDurably(77L, 88L, fixture.notification);

        Mockito.verify(fixture.queue).enqueue(101L, Collections.emptyList());
    }

    private static Fixture fixture() {
        return new Fixture(Mockito.mock(CustomerExternalIdentityRepository.class),
                Mockito.mock(CustomerNotificationRecorder.class),
                Mockito.mock(CommunicationEndpointService.class),
                Mockito.mock(NotificationPreferenceService.class),
                Mockito.mock(NotificationDeliveryQueue.class),
                new ReferralUnlockedCustomerNotification("ALEX7K2"));
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

    private record Fixture(CustomerExternalIdentityRepository repository,
                           CustomerNotificationRecorder recorder,
                           CommunicationEndpointService endpoints,
                           NotificationPreferenceService preferences,
                           NotificationDeliveryQueue queue,
                           CustomerNotification notification) {
        CustomerNotificationDispatcher dispatcher() {
            return new CustomerNotificationDispatcher(repository, recorder, endpoints, preferences, queue);
        }
    }
}
