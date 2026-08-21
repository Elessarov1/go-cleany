package com.cleany.telegram.bot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.notification.CommunicationTarget;
import com.cleany.notification.ReferralUnlockedCustomerNotification;

class TelegramCustomerNotificationSenderTest {

    @Test
    void telegramTarget_sentToExternalSubjectWithPreferredLanguage() {
        TelegramCustomerNotificationMessageFactory messageFactory =
                Mockito.mock(TelegramCustomerNotificationMessageFactory.class);
        TelegramBotClient botClient = Mockito.mock(TelegramBotClient.class);
        Mockito.when(messageFactory.referralUnlocked("ALEX7K2", "en"))
                .thenReturn("Referral unlocked");
        var sender = new TelegramCustomerNotificationSender(
                messageFactory,
                botClient
        );

        sender.send(
                new CommunicationTarget(
                        77L,
                        88L,
                        ExternalIdentityProvider.TELEGRAM,
                        "900001",
                        "en"
                ),
                new ReferralUnlockedCustomerNotification("ALEX7K2")
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(ExternalIdentityProvider.TELEGRAM, sender.provider()),
                () -> Mockito.verify(botClient).sendMessage(
                        900001L,
                        "Referral unlocked",
                        TelegramBotClient.InlineKeyboard.empty()
                )
        );
    }

    @Test
    void nonTelegramTarget_rejected() {
        TelegramCustomerNotificationMessageFactory messageFactory =
                Mockito.mock(TelegramCustomerNotificationMessageFactory.class);
        TelegramBotClient botClient = Mockito.mock(TelegramBotClient.class);
        var sender = new TelegramCustomerNotificationSender(
                messageFactory,
                botClient
        );

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> sender.send(
                        new CommunicationTarget(
                                77L,
                                88L,
                                ExternalIdentityProvider.WHATSAPP,
                                "905551234567",
                                "ru"
                        ),
                        new ReferralUnlockedCustomerNotification("ALEX7K2")
                )
        );

        Mockito.verifyNoInteractions(messageFactory, botClient);
    }
}
