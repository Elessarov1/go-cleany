package com.cleany.configuration;

import java.net.URI;
import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TelegramPropertiesTest {

    @Test
    void pollingMode_doesNotRequireWebhookSecret() {
        Assertions.assertDoesNotThrow(() -> properties(TelegramUpdateMode.POLLING, ""));
    }

    @Test
    void webhookMode_requiresValidWebhookSecret() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> properties(TelegramUpdateMode.WEBHOOK, "")
        );
    }

    private static TelegramProperties properties(TelegramUpdateMode mode, String webhookSecret) {
        return new TelegramProperties(
                "123456789:test-token",
                webhookSecret,
                Duration.ofHours(1),
                Duration.ofSeconds(30),
                true,
                URI.create("https://api.telegram.org"),
                mode,
                25,
                Duration.ofSeconds(3),
                false
        );
    }
}
