package com.cleany.authentication;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("native-auth")
public record NativeAuthProperties(
        boolean enabled,
        Duration accessTtl,
        Duration refreshTtl,
        Duration absoluteTtl,
        Duration refreshRetryTtl,
        Duration challengeTtl,
        Duration sensitiveProofTtl,
        String tokenEncryptionKey,
        List<String> googleAudiences,
        List<String> appleAudiences,
        String telegramBotUsername
) {
    public NativeAuthProperties {
        accessTtl = accessTtl == null ? Duration.ofMinutes(15) : accessTtl;
        refreshTtl = refreshTtl == null ? Duration.ofDays(30) : refreshTtl;
        absoluteTtl = absoluteTtl == null ? Duration.ofDays(90) : absoluteTtl;
        refreshRetryTtl = refreshRetryTtl == null ? Duration.ofMinutes(2) : refreshRetryTtl;
        challengeTtl = challengeTtl == null ? Duration.ofMinutes(10) : challengeTtl;
        sensitiveProofTtl = sensitiveProofTtl == null ? Duration.ofMinutes(5) : sensitiveProofTtl;
        tokenEncryptionKey = normalize(tokenEncryptionKey);
        googleAudiences = googleAudiences == null ? Collections.emptyList() : googleAudiences.stream()
                .filter(value -> value != null && !value.isBlank()).map(String::trim).toList();
        appleAudiences = appleAudiences == null ? Collections.emptyList() : appleAudiences.stream()
                .filter(value -> value != null && !value.isBlank()).map(String::trim).toList();
        telegramBotUsername = normalize(telegramBotUsername);
        if (accessTtl.isNegative() || accessTtl.isZero()
                || refreshTtl.isNegative() || refreshTtl.isZero()
                || absoluteTtl.compareTo(refreshTtl) < 0
                || refreshRetryTtl.isNegative() || refreshRetryTtl.isZero()
                || challengeTtl.isNegative() || challengeTtl.isZero()
                || sensitiveProofTtl.isNegative() || sensitiveProofTtl.isZero()) {
            throw new IllegalArgumentException("Native auth durations are invalid");
        }
        if (enabled && tokenEncryptionKey == null) {
            throw new IllegalArgumentException("NATIVE_AUTH_TOKEN_ENCRYPTION_KEY is required");
        }
        if (enabled && (telegramBotUsername == null
                || !telegramBotUsername.replaceFirst("^@", "").matches("[A-Za-z0-9_]{5,32}"))) {
            throw new IllegalArgumentException("NATIVE_AUTH_TELEGRAM_BOT_USERNAME is invalid");
        }
        if (telegramBotUsername != null) telegramBotUsername = telegramBotUsername.replaceFirst("^@", "");
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
