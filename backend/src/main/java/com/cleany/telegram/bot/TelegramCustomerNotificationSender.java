package com.cleany.telegram.bot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.cleany.configuration.PublicApplicationProperties;
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitCustomerNotification;
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
import com.cleany.transfer.TransferAdminNewRequestNotification;
import com.cleany.transfer.TransferBookingCustomerNotification;

@Component
@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
public class TelegramCustomerNotificationSender implements CustomerNotificationSender {

    private final TelegramCustomerNotificationMessageFactory messageFactory;
    private final CleaningOrderBotMessageFactory cleaningMessageFactory;
    private final TelegramBotClient botClient;
    private final MediaProviderReferenceService mediaProviderReferenceService;
    private final PublicApplicationProperties publicApplicationProperties;

    @Autowired
    public TelegramCustomerNotificationSender(
            TelegramCustomerNotificationMessageFactory messageFactory,
            CleaningOrderBotMessageFactory cleaningMessageFactory,
            TelegramBotClient botClient,
            MediaProviderReferenceService mediaProviderReferenceService,
            PublicApplicationProperties publicApplicationProperties
    ) {
        this.messageFactory = messageFactory;
        this.cleaningMessageFactory = cleaningMessageFactory;
        this.botClient = botClient;
        this.mediaProviderReferenceService = mediaProviderReferenceService;
        this.publicApplicationProperties = publicApplicationProperties;
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
        if (notification instanceof RentalCleaningBenefitCustomerNotification benefit) {
            sendMessage(
                    telegramUserId,
                    messageFactory.rentalCleaningBenefit(benefit, target.languageCode())
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
            botClient.sendMessage(
                    telegramUserId,
                    cleaningMessageFactory.customerReportReady(completed),
                    TelegramBotClient.InlineKeyboard.ofRows(List.of(
                            TelegramBotClient.InlineButton.url(
                                    "Открыть отчёт",
                                    publicApplicationProperties.baseUrl()
                                            + "/cleaning/orders/" + completed.orderId()
                            )
                    ))
            );
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
        if (notification instanceof TransferBookingCustomerNotification transfer) {
            sendMessage(
                    telegramUserId,
                    messageFactory.transferCustomer(transfer, target.languageCode())
            );
            return;
        }
        if (notification instanceof TransferAdminNewRequestNotification transferAdmin) {
            botClient.sendMessage(
                    telegramUserId,
                    messageFactory.transferAdminRequested(transferAdmin, target.languageCode()),
                    TelegramBotClient.InlineKeyboard.ofRows(List.of(
                            TelegramBotClient.InlineButton.url(
                                    "Открыть заявку",
                                    publicApplicationProperties.baseUrl() + transferAdmin.targetPath()
                            )
                    ))
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
