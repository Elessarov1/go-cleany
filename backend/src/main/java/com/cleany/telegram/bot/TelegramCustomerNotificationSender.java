package com.cleany.telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.notification.CustomerNotificationSender;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Component
public class TelegramCustomerNotificationSender implements CustomerNotificationSender {

    private static final Logger log = LoggerFactory.getLogger(TelegramCustomerNotificationSender.class);

    private final CustomerExternalIdentityRepository identityRepository;
    private final TelegramCustomerNotificationMessageFactory messageFactory;
    private final TelegramBotClient botClient;

    public TelegramCustomerNotificationSender(
            CustomerExternalIdentityRepository identityRepository,
            TelegramCustomerNotificationMessageFactory messageFactory,
            TelegramBotClient botClient
    ) {
        this.identityRepository = identityRepository;
        this.messageFactory = messageFactory;
        this.botClient = botClient;
    }

    @Override
    public void sendReferralUnlocked(long customerId, String referralCode) {
        var identity = identityRepository.findByCustomerIdAndProvider(
                customerId,
                ExternalIdentityProvider.TELEGRAM
        );
        if (identity.isEmpty()) {
            log.warn("Telegram identity is unavailable for referral unlock customer {}", customerId);
            return;
        }

        long telegramUserId;
        try {
            telegramUserId = Long.parseLong(identity.get().getExternalSubject());
        } catch (NumberFormatException exception) {
            log.error("Telegram identity is invalid for referral unlock customer {}", customerId);
            return;
        }

        botClient.sendMessage(
                telegramUserId,
                messageFactory.referralUnlocked(referralCode, identity.get().getLanguageCode()),
                TelegramBotClient.InlineKeyboard.empty()
        );
    }
}
