package com.cleany.telegram.bot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.media.MediaProvider;
import com.cleany.media.MediaProviderReferenceService;
import com.cleany.notification.CommunicationTarget;
import com.cleany.notification.CustomerNotification;
import com.cleany.notification.CustomerNotificationSender;
import com.cleany.notification.ExternalMediaReference;
import com.cleany.notification.ReferralUnlockedCustomerNotification;
import com.cleany.order.CleaningOrderCustomerNotification;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Component
public class TelegramCustomerNotificationSender implements CustomerNotificationSender {

    private final TelegramCustomerNotificationMessageFactory messageFactory;
    private final CleaningOrderBotMessageFactory cleaningMessageFactory;
    private final TelegramBotClient botClient;
    private final MediaProviderReferenceService mediaProviderReferenceService;

    public TelegramCustomerNotificationSender(
            TelegramCustomerNotificationMessageFactory messageFactory,
            CleaningOrderBotMessageFactory cleaningMessageFactory,
            TelegramBotClient botClient,
            MediaProviderReferenceService mediaProviderReferenceService
    ) {
        this.messageFactory = messageFactory;
        this.cleaningMessageFactory = cleaningMessageFactory;
        this.botClient = botClient;
        this.mediaProviderReferenceService = mediaProviderReferenceService;
    }

    @Override
    public ExternalIdentityProvider provider() {
        return ExternalIdentityProvider.TELEGRAM;
    }

    @Override
    public void send(CommunicationTarget target, CustomerNotification notification) {
        if (target.provider() != provider()) {
            throw new IllegalArgumentException("Telegram sender received a non-Telegram target");
        }

        long telegramUserId;
        try {
            telegramUserId = Long.parseLong(target.externalSubject());
            if (telegramUserId <= 0) {
                throw new NumberFormatException("Telegram user id must be positive");
            }
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Telegram target external subject is invalid", exception);
        }

        if (notification instanceof ReferralUnlockedCustomerNotification unlocked) {
            sendMessage(
                    telegramUserId,
                    messageFactory.referralUnlocked(
                            unlocked.referralCode(),
                            target.languageCode()
                    )
            );
            return;
        }
        if (notification instanceof CleaningOrderCustomerNotification.Accepted) {
            sendMessage(telegramUserId, cleaningMessageFactory.customerOrderAccepted());
            return;
        }
        if (notification instanceof CleaningOrderCustomerNotification.Cancelled) {
            sendMessage(telegramUserId, cleaningMessageFactory.customerOrderCancelled());
            return;
        }
        if (notification instanceof CleaningOrderCustomerNotification.Completed completed) {
            validateTelegramMedia(completed.photos());
            sendMessage(telegramUserId, cleaningMessageFactory.customerReportHeader(completed));
            completed.photos().forEach(photo -> botClient.sendPhoto(telegramUserId, photo.externalId()));
            sendMessage(telegramUserId, cleaningMessageFactory.customerReportComment(completed));
            return;
        }
        if (notification instanceof CleaningOrderCustomerNotification.OnsiteIssueReported issue) {
            var photos = issue.mediaIds().stream()
                    .map(mediaId -> mediaProviderReferenceService.require(mediaId, MediaProvider.TELEGRAM))
                    .toList();
            sendMessage(
                    telegramUserId,
                    cleaningMessageFactory.customerOnsiteIssueReport(issue.reason(), issue.comment())
            );
            photos.forEach(photo -> botClient.sendPhoto(telegramUserId, photo.externalId()));
            sendMessage(telegramUserId, cleaningMessageFactory.customerOnsiteIssuePaused());
            return;
        }
        throw new IllegalArgumentException(
                "Unsupported Telegram customer notification: " + notification.getClass().getName()
        );
    }

    private void sendMessage(long telegramUserId, String message) {
        botClient.sendMessage(telegramUserId, message, TelegramBotClient.InlineKeyboard.empty());
    }

    private void validateTelegramMedia(Iterable<ExternalMediaReference> media) {
        for (ExternalMediaReference reference : media) {
            if (reference.provider() != MediaProvider.TELEGRAM) {
                throw new IllegalArgumentException("Telegram sender received non-Telegram media");
            }
        }
    }
}
