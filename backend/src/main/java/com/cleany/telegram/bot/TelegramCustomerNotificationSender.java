package com.cleany.telegram.bot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.notification.CommunicationTarget;
import com.cleany.notification.CustomerNotification;
import com.cleany.notification.CustomerNotificationSender;
import com.cleany.notification.ReferralUnlockedCustomerNotification;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Component
public class TelegramCustomerNotificationSender implements CustomerNotificationSender {

    private final TelegramCustomerNotificationMessageFactory messageFactory;
    private final TelegramBotClient botClient;

    public TelegramCustomerNotificationSender(
            TelegramCustomerNotificationMessageFactory messageFactory,
            TelegramBotClient botClient
    ) {
        this.messageFactory = messageFactory;
        this.botClient = botClient;
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

        String message = switch (notification) {
            case ReferralUnlockedCustomerNotification unlocked -> messageFactory.referralUnlocked(
                    unlocked.referralCode(),
                    target.languageCode()
            );
        };
        botClient.sendMessage(
                telegramUserId,
                message,
                TelegramBotClient.InlineKeyboard.empty()
        );
    }
}
