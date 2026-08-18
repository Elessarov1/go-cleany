package com.cleany.configuration;

import java.net.URI;
import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "telegram")
public record TelegramProperties(
        @NotBlank String botToken,
        String webhookSecret,
        @NotNull Duration initDataMaxAge,
        @NotNull Duration initDataAllowedClockSkew,
        boolean botEnabled,
        @NotNull URI apiBaseUrl,
        @NotNull TelegramUpdateMode updateMode,
        int pollingTimeoutSeconds,
        @NotNull Duration pollingRetryDelay,
        boolean pollingDropPendingUpdates
) {

    public TelegramProperties {
        if (initDataMaxAge != null && (initDataMaxAge.isZero() || initDataMaxAge.isNegative())) {
            throw new IllegalArgumentException("telegram.init-data-max-age must be positive");
        }
        if (initDataAllowedClockSkew != null && initDataAllowedClockSkew.isNegative()) {
            throw new IllegalArgumentException("telegram.init-data-allowed-clock-skew must not be negative");
        }
        if (botEnabled
                && updateMode == TelegramUpdateMode.WEBHOOK
                && (webhookSecret == null || !webhookSecret.matches("[A-Za-z0-9_-]{1,256}"))) {
            throw new IllegalArgumentException(
                    "telegram.webhook-secret must contain only A-Z, a-z, 0-9, underscore, or hyphen"
            );
        }
        if (botEnabled && (botToken == null || !botToken.matches("[0-9]+:[A-Za-z0-9_-]+"))) {
            throw new IllegalArgumentException("telegram.bot-token has an invalid Bot API token format");
        }
        if (apiBaseUrl != null
                && (!"https".equalsIgnoreCase(apiBaseUrl.getScheme()) || apiBaseUrl.getHost() == null)) {
            throw new IllegalArgumentException("telegram.api-base-url must be an absolute HTTPS URL");
        }
        if (pollingTimeoutSeconds < 1 || pollingTimeoutSeconds > 50) {
            throw new IllegalArgumentException("telegram.polling-timeout-seconds must be between 1 and 50");
        }
        if (pollingRetryDelay != null
                && (pollingRetryDelay.isNegative() || pollingRetryDelay.isZero())) {
            throw new IllegalArgumentException("telegram.polling-retry-delay must be positive");
        }
    }
}
