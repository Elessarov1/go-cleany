package com.cleany.telegram;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cleany.local-auth")
public record LocalAuthProperties(
        long telegramUserId,
        String username,
        String firstName,
        String lastName
) {
}

