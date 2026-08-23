package com.cleany.telegram.bot;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.media.MediaProvider;
import com.cleany.media.MediaProviderReferenceData;
import com.cleany.media.MediaProviderReferenceService;
import com.cleany.notification.CommunicationTarget;
import com.cleany.notification.CustomerNotification;
import com.cleany.notification.CustomerNotificationSender;
import com.cleany.notification.ReferralUnlockedCustomerNotification;
import com.cleany.order.CleaningOrderCustomerNotification;
import com.cleany.rental.RentalBookingCustomerNotification;

import lombok.RequiredArgsConstructor;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Component
@RequiredArgsConstructor
public class TelegramCustomerNotificationSender implements CustomerNotificationSender {

    private final TelegramCustomerNotificationMessageFactory messageFactory;
    private final CleaningOrderBotMessageFactory cleaningMessageFactory;
    private final TelegramBotClient botClient;
    private final MediaProviderReferenceService mediaProviderReferenceService;

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
            var photos = telegramPhotos(completed.mediaIds());
            sendMessage(telegramUserId, cleaningMessageFactory.customerReportHeader(completed));
            photos.forEach(photo -> botClient.sendPhoto(telegramUserId, photo.externalId()));
            sendMessage(telegramUserId, cleaningMessageFactory.customerReportComment(completed));
            return;
        }
        if (notification instanceof CleaningOrderCustomerNotification.OnsiteIssueReported issue) {
            var photos = telegramPhotos(issue.mediaIds());
            sendMessage(
                    telegramUserId,
                    cleaningMessageFactory.customerOnsiteIssueReport(issue.reason(), issue.comment())
            );
            photos.forEach(photo -> botClient.sendPhoto(telegramUserId, photo.externalId()));
            sendMessage(telegramUserId, cleaningMessageFactory.customerOnsiteIssuePaused());
            return;
        }
        if (notification instanceof RentalBookingCustomerNotification.Confirmed confirmed) {
            sendMessage(
                    telegramUserId,
                    messageFactory.rentalConfirmed(confirmed, target.languageCode())
            );
            return;
        }
        if (notification instanceof RentalBookingCustomerNotification.Cancelled cancelled) {
            sendMessage(
                    telegramUserId,
                    messageFactory.rentalCancelled(cancelled, target.languageCode())
            );
            return;
        }
        throw new IllegalArgumentException(
                "Unsupported Telegram customer notification: " + notification.getClass().getName()
        );
    }

    private void sendMessage(long telegramUserId, String message) {
        botClient.sendMessage(telegramUserId, message, TelegramBotClient.InlineKeyboard.empty());
    }

    private List<MediaProviderReferenceData> telegramPhotos(List<Long> mediaIds) {
        return mediaIds.stream()
                .map(mediaId -> mediaProviderReferenceService.require(mediaId, MediaProvider.TELEGRAM))
                .toList();
    }
}
