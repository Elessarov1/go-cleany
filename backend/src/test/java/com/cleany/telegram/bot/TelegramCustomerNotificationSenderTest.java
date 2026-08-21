package com.cleany.telegram.bot;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.notification.CommunicationTarget;
import com.cleany.notification.ExternalMediaReference;
import com.cleany.notification.ReferralUnlockedCustomerNotification;
import com.cleany.order.ApartmentType;
import com.cleany.order.CleaningOrderCustomerNotification;
import com.cleany.order.OnsiteIssueReason;
import com.cleany.order.ServiceArea;

class TelegramCustomerNotificationSenderTest {

    @Test
    void telegramTarget_sentToExternalSubjectWithPreferredLanguage() {
        TelegramCustomerNotificationMessageFactory messageFactory =
                Mockito.mock(TelegramCustomerNotificationMessageFactory.class);
        CleaningOrderBotMessageFactory cleaningMessageFactory =
                Mockito.mock(CleaningOrderBotMessageFactory.class);
        TelegramBotClient botClient = Mockito.mock(TelegramBotClient.class);
        Mockito.when(messageFactory.referralUnlocked("ALEX7K2", "en"))
                .thenReturn("Referral unlocked");
        var sender = new TelegramCustomerNotificationSender(
                messageFactory,
                cleaningMessageFactory,
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
        CleaningOrderBotMessageFactory cleaningMessageFactory =
                Mockito.mock(CleaningOrderBotMessageFactory.class);
        TelegramBotClient botClient = Mockito.mock(TelegramBotClient.class);
        var sender = new TelegramCustomerNotificationSender(
                messageFactory,
                cleaningMessageFactory,
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

        Mockito.verifyNoInteractions(messageFactory, cleaningMessageFactory, botClient);
    }

    @Test
    void orderStatusNotifications_renderedByCleaningMessageFactory() {
        TelegramCustomerNotificationMessageFactory messageFactory =
                Mockito.mock(TelegramCustomerNotificationMessageFactory.class);
        CleaningOrderBotMessageFactory cleaningMessageFactory =
                Mockito.mock(CleaningOrderBotMessageFactory.class);
        TelegramBotClient botClient = Mockito.mock(TelegramBotClient.class);
        Mockito.when(cleaningMessageFactory.customerOrderAccepted()).thenReturn("accepted");
        Mockito.when(cleaningMessageFactory.customerOrderCancelled()).thenReturn("cancelled");
        var sender = new TelegramCustomerNotificationSender(
                messageFactory,
                cleaningMessageFactory,
                botClient
        );

        sender.send(telegramTarget(), new CleaningOrderCustomerNotification.Accepted(43L));
        sender.send(telegramTarget(), new CleaningOrderCustomerNotification.Cancelled(43L));

        var order = Mockito.inOrder(botClient);
        order.verify(botClient).sendMessage(900001L, "accepted", TelegramBotClient.InlineKeyboard.empty());
        order.verify(botClient).sendMessage(900001L, "cancelled", TelegramBotClient.InlineKeyboard.empty());
    }

    @Test
    void completedCleaningReport_messagesAndPhotosSentInOrder() {
        TelegramCustomerNotificationMessageFactory messageFactory =
                Mockito.mock(TelegramCustomerNotificationMessageFactory.class);
        CleaningOrderBotMessageFactory cleaningMessageFactory =
                Mockito.mock(CleaningOrderBotMessageFactory.class);
        TelegramBotClient botClient = Mockito.mock(TelegramBotClient.class);
        var notification = new CleaningOrderCustomerNotification.Completed(
                43L,
                ApartmentType.TWO_PLUS_ONE,
                false,
                ServiceArea.MAHMUTLAR,
                LocalDate.of(2026, 8, 18),
                "Готово",
                List.of(
                        ExternalMediaReference.telegram("photo-1"),
                        ExternalMediaReference.telegram("photo-2")
                )
        );
        Mockito.when(cleaningMessageFactory.customerReportHeader(notification)).thenReturn("header");
        Mockito.when(cleaningMessageFactory.customerReportComment(notification)).thenReturn("comment");
        var sender = new TelegramCustomerNotificationSender(
                messageFactory,
                cleaningMessageFactory,
                botClient
        );

        sender.send(telegramTarget(), notification);

        var order = Mockito.inOrder(botClient);
        order.verify(botClient).sendMessage(900001L, "header", TelegramBotClient.InlineKeyboard.empty());
        order.verify(botClient).sendPhoto(900001L, "photo-1");
        order.verify(botClient).sendPhoto(900001L, "photo-2");
        order.verify(botClient).sendMessage(900001L, "comment", TelegramBotClient.InlineKeyboard.empty());
    }

    @Test
    void onsiteIssue_reportPhotosAndPausedMessageSentInOrder() {
        TelegramCustomerNotificationMessageFactory messageFactory =
                Mockito.mock(TelegramCustomerNotificationMessageFactory.class);
        CleaningOrderBotMessageFactory cleaningMessageFactory =
                Mockito.mock(CleaningOrderBotMessageFactory.class);
        TelegramBotClient botClient = Mockito.mock(TelegramBotClient.class);
        var notification = new CleaningOrderCustomerNotification.OnsiteIssueReported(
                43L,
                OnsiteIssueReason.ACCESS_PROBLEM,
                "Нет ключа",
                List.of(ExternalMediaReference.telegram("evidence-1"))
        );
        Mockito.when(cleaningMessageFactory.customerOnsiteIssueReport(
                OnsiteIssueReason.ACCESS_PROBLEM,
                "Нет ключа"
        )).thenReturn("issue");
        Mockito.when(cleaningMessageFactory.customerOnsiteIssuePaused()).thenReturn("paused");
        var sender = new TelegramCustomerNotificationSender(
                messageFactory,
                cleaningMessageFactory,
                botClient
        );

        sender.send(telegramTarget(), notification);

        var order = Mockito.inOrder(botClient);
        order.verify(botClient).sendMessage(900001L, "issue", TelegramBotClient.InlineKeyboard.empty());
        order.verify(botClient).sendPhoto(900001L, "evidence-1");
        order.verify(botClient).sendMessage(900001L, "paused", TelegramBotClient.InlineKeyboard.empty());
    }

    private static CommunicationTarget telegramTarget() {
        return new CommunicationTarget(
                77L,
                88L,
                ExternalIdentityProvider.TELEGRAM,
                "900001",
                "ru"
        );
    }
}
