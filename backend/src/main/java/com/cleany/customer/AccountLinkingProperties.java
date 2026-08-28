package com.cleany.customer;

import java.time.Duration;

import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "account-linking.telegram")
public record AccountLinkingProperties(
        String miniAppLinkBase,
        @NotNull Duration requestTtl
) {

    public AccountLinkingProperties {
        miniAppLinkBase = miniAppLinkBase == null ? "" : miniAppLinkBase.trim();
        if (requestTtl == null || requestTtl.isZero() || requestTtl.isNegative()) {
            throw new IllegalArgumentException("account-linking.telegram.request-ttl must be positive");
        }
    }
}
