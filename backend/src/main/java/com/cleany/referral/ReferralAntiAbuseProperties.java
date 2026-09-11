package com.cleany.referral;

import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "referral.anti-abuse")
public record ReferralAntiAbuseProperties(
        @NotBlank String hmacKey,
        @NotNull Duration markerRetention
) {
    public ReferralAntiAbuseProperties {
        if (markerRetention != null && (markerRetention.isZero() || markerRetention.isNegative())) {
            throw new IllegalArgumentException("referral.anti-abuse.marker-retention must be positive");
        }
    }
}
