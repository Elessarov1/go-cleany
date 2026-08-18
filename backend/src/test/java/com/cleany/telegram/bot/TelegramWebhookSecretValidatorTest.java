package com.cleany.telegram.bot;

import java.net.URI;
import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.cleany.configuration.TelegramProperties;
import com.cleany.configuration.TelegramUpdateMode;

class TelegramWebhookSecretValidatorTest {

    private final TelegramWebhookSecretValidator validator = new TelegramWebhookSecretValidator(
            new TelegramProperties(
                    "123456789:test-token",
                    "expected_secret-1",
                    Duration.ofHours(1),
                    Duration.ofSeconds(30),
                    true,
                    URI.create("https://api.telegram.org"),
                    TelegramUpdateMode.WEBHOOK,
                    25,
                    Duration.ofSeconds(3),
                    false
            )
    );

    @Test
    void expectedSecret_authenticationAccepted() {
        Assertions.assertDoesNotThrow(() -> validator.validate("expected_secret-1"));
    }

    @Test
    void missingOrDifferentSecret_authenticationRejected() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        TelegramWebhookAuthenticationException.class,
                        () -> validator.validate(null)
                ),
                () -> Assertions.assertThrows(
                        TelegramWebhookAuthenticationException.class,
                        () -> validator.validate("different-secret")
                )
        );
    }
}
