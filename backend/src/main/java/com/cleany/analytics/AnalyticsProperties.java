package com.cleany.analytics;

import java.time.Instant;
import java.time.ZoneId;

import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("analytics")
public record AnalyticsProperties(
        @NotNull ZoneId zoneId,
        Instant commercialLaunchAt
) {
}
