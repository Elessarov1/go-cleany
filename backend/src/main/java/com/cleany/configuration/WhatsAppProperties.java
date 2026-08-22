package com.cleany.configuration;

import java.net.URI;

import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "whatsapp")
public record WhatsAppProperties(
        boolean enabled,
        @NotNull URI graphApiBaseUrl,
        String graphApiVersion,
        String appId,
        String businessPortfolioId,
        String businessAccountId,
        String phoneNumberId,
        String accessToken,
        String appSecret,
        String webhookVerifyToken,
        boolean testReplyEnabled
) {

    public WhatsAppProperties {
        if (graphApiBaseUrl != null
                && (!"https".equalsIgnoreCase(graphApiBaseUrl.getScheme())
                || graphApiBaseUrl.getHost() == null)) {
            throw new IllegalArgumentException("whatsapp.graph-api-base-url must be an absolute HTTPS URL");
        }
        if (graphApiVersion == null || !graphApiVersion.matches("v[0-9]+\\.[0-9]+")) {
            throw new IllegalArgumentException("whatsapp.graph-api-version must use the vN.N format");
        }
        if (enabled) {
            requireNumericId(appId, "whatsapp.app-id");
            requireNumericId(businessPortfolioId, "whatsapp.business-portfolio-id");
            requireNumericId(businessAccountId, "whatsapp.business-account-id");
            requireNumericId(phoneNumberId, "whatsapp.phone-number-id");
            requireText(accessToken, "whatsapp.access-token");
            requireText(appSecret, "whatsapp.app-secret");
            if (webhookVerifyToken == null
                    || !webhookVerifyToken.matches("[A-Za-z0-9_-]{32,256}")) {
                throw new IllegalArgumentException(
                        "whatsapp.webhook-verify-token must contain 32-256 URL-safe characters"
                );
            }
        }
    }

    private static void requireNumericId(String value, String property) {
        if (value == null || !value.matches("[0-9]+")) {
            throw new IllegalArgumentException(property + " must be a numeric Meta ID");
        }
    }

    private static void requireText(String value, String property) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(property + " must not be blank");
        }
    }
}
