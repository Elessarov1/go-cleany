package com.cleany.transfer;

import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("transfer.driver-link")
public record TransferDriverLinkProperties(
        @NotBlank String botUsername,
        @NotNull Duration tokenTtl
) {

    public TransferDriverLinkProperties {
        botUsername = botUsername == null ? null : botUsername.trim().replaceFirst("^@", "");
        if (botUsername == null || !botUsername.matches("[A-Za-z0-9_]{5,32}")) {
            throw new IllegalArgumentException("transfer.driver-link.bot-username is invalid");
        }
        if (tokenTtl == null || tokenTtl.isZero() || tokenTtl.isNegative()) {
            throw new IllegalArgumentException("transfer.driver-link.token-ttl must be positive");
        }
    }
}
