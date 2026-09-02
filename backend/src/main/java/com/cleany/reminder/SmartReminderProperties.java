package com.cleany.reminder;

import java.time.ZoneId;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("smart-reminders")
public record SmartReminderProperties(
        boolean enabled,
        @NotBlank String cron,
        @NotNull ZoneId zoneId,
        @Min(1) int batchSize,
        @Min(1) int cleaningGraceDays,
        @Min(1) int rentalTransferDaysBefore,
        @Min(1) int transferDaysBefore
) {
}
