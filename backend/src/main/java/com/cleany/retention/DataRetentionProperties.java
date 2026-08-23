package com.cleany.retention;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "data-retention")
public record DataRetentionProperties(
        boolean enabled,
        @Min(1) int days,
        @NotBlank String cron,
        @Min(1) int batchSize,
        @Min(1) int maxBatchesPerRun
) {
}
