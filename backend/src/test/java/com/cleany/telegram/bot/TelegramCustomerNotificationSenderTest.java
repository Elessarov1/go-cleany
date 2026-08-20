package com.cleany.telegram.bot;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.customer.CustomerExternalIdentity;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;

class TelegramCustomerNotificationSenderTest {

    @Test
    void customerId_resolvedToTelegramRecipientAndPreferredLanguage() {
        CustomerExternalIdentityRepository identityRepository =
                Mockito.mock(CustomerExternalIdentityRepository.class);
        TelegramCustomerNotificationMessageFactory messageFactory =
                Mockito.mock(TelegramCustomerNotificationMessageFactory.class);
        TelegramBotClient botClient = Mockito.mock(TelegramBotClient.class);
        CustomerExternalIdentity identity = Mockito.mock(CustomerExternalIdentity.class);
        Mockito.when(identity.getExternalSubject()).thenReturn("900001");
        Mockito.when(identity.getLanguageCode()).thenReturn("en");
        Mockito.when(identityRepository.findByCustomerIdAndProvider(
                77L,
                ExternalIdentityProvider.TELEGRAM
        )).thenReturn(Optional.of(identity));
        Mockito.when(messageFactory.referralUnlocked("ALEX7K2", "en"))
                .thenReturn("Referral unlocked");
        var sender = new TelegramCustomerNotificationSender(
                identityRepository,
                messageFactory,
                botClient
        );

        sender.sendReferralUnlocked(77L, "ALEX7K2");

        Mockito.verify(botClient).sendMessage(
                900001L,
                "Referral unlocked",
                TelegramBotClient.InlineKeyboard.empty()
        );
    }

    @Test
    void customerWithoutTelegramIdentity_notificationSkipped() {
        CustomerExternalIdentityRepository identityRepository =
                Mockito.mock(CustomerExternalIdentityRepository.class);
        TelegramCustomerNotificationMessageFactory messageFactory =
                Mockito.mock(TelegramCustomerNotificationMessageFactory.class);
        TelegramBotClient botClient = Mockito.mock(TelegramBotClient.class);
        Mockito.when(identityRepository.findByCustomerIdAndProvider(
                77L,
                ExternalIdentityProvider.TELEGRAM
        )).thenReturn(Optional.empty());
        var sender = new TelegramCustomerNotificationSender(
                identityRepository,
                messageFactory,
                botClient
        );

        sender.sendReferralUnlocked(77L, "ALEX7K2");

        Mockito.verifyNoInteractions(messageFactory, botClient);
    }
}
