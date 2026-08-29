package com.cleany.transfer;

import java.time.ZoneId;
import java.util.Objects;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("transfer")
public record TransferProperties(
        @Min(1) int minBookingDaysAhead,
        @Min(1) int bookingMonthsAhead,
        @Min(5) @Max(1440) int timeSlotMinutes,
        @NotNull ZoneId zoneId,
        @NotNull TransferAssignmentMode assignmentMode
) {

    public TransferProperties {
        Objects.requireNonNull(zoneId, "zoneId");
        Objects.requireNonNull(assignmentMode, "assignmentMode");
        if (1440 % timeSlotMinutes != 0) {
            throw new InvalidTransferConfigurationException(
                    "Transfer time slot must divide a 24-hour day without remainder"
            );
        }
    }
}
