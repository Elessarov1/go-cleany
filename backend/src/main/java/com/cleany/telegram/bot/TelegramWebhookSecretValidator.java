package com.cleany.telegram.bot;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.cleany.configuration.TelegramProperties;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@ConditionalOnProperty(prefix = "telegram", name = "update-mode", havingValue = "webhook")
@Component
public class TelegramWebhookSecretValidator {

    private final byte[] expectedSecret;

    public TelegramWebhookSecretValidator(TelegramProperties properties) {
        expectedSecret = properties.webhookSecret().getBytes(StandardCharsets.UTF_8);
    }

    public void validate(String suppliedSecret) {
        byte[] suppliedBytes = suppliedSecret == null
                ? new byte[0]
                : suppliedSecret.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expectedSecret, suppliedBytes)) {
            throw new TelegramWebhookAuthenticationException();
        }
    }
}
