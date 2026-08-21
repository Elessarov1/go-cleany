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
    void communicationIdentity_selectsExactlyOneMatchingProvider() {
        CustomerExternalIdentityRepository identityRepository =
                Mockito.mock(CustomerExternalIdentityRepository.class);
        CustomerNotificationSender telegramSender = Mockito.mock(CustomerNotificationSender.class);
        CustomerNotificationSender whatsappSender = Mockito.mock(CustomerNotificationSender.class);
        CustomerExternalIdentity identity = Mockito.mock(CustomerExternalIdentity.class);
        Mockito.when(telegramSender.provider()).thenReturn(ExternalIdentityProvider.TELEGRAM);
        Mockito.when(whatsappSender.provider()).thenReturn(ExternalIdentityProvider.WHATSAPP);
        Mockito.when(identityRepository.findByIdAndCustomerId(88L, 77L))
                .thenReturn(Optional.of(identity));
        Mockito.when(identity.getCustomerId()).thenReturn(77L);
        Mockito.when(identity.getProvider()).thenReturn(ExternalIdentityProvider.WHATSAPP);
        Mockito.when(identity.getExternalSubject()).thenReturn("905551234567");
        Mockito.when(identity.getLanguageCode()).thenReturn("ru");
        var dispatcher = new CustomerNotificationDispatcher(
                identityRepository,
                List.of(telegramSender, whatsappSender)
        );
        var notification = new ReferralUnlockedCustomerNotification("ALEX7K2");

        boolean delivered = dispatcher.send(77L, 88L, notification);

        Assertions.assertAll(
                () -> Assertions.assertTrue(delivered),
                () -> Mockito.verify(whatsappSender).send(
                        new CommunicationTarget(
                                77L,
                                88L,
                                ExternalIdentityProvider.WHATSAPP,
                                "905551234567",
                                "ru"
                        ),
                        notification
                )
        );
        Mockito.verify(telegramSender, Mockito.never())
                .send(Mockito.any(), Mockito.any());
    }

    @Test
    void providerWithoutSender_deliveryReportedAsUnavailable() {
        CustomerExternalIdentityRepository identityRepository =
                Mockito.mock(CustomerExternalIdentityRepository.class);
        CustomerExternalIdentity identity = Mockito.mock(CustomerExternalIdentity.class);
        Mockito.when(identityRepository.findByIdAndCustomerId(88L, 77L))
                .thenReturn(Optional.of(identity));
        Mockito.when(identity.getCustomerId()).thenReturn(77L);
        Mockito.when(identity.getProvider()).thenReturn(ExternalIdentityProvider.WHATSAPP);
        Mockito.when(identity.getExternalSubject()).thenReturn("905551234567");
        var dispatcher = new CustomerNotificationDispatcher(identityRepository, List.of());

        boolean delivered = dispatcher.send(
                77L,
                88L,
                new ReferralUnlockedCustomerNotification("ALEX7K2")
        );

        Assertions.assertFalse(delivered);
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
                        List.of(first, second)
                )
        );
    }
}
