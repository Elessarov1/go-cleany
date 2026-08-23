package com.cleany.rental;

import java.math.BigDecimal;
import java.time.ZoneId;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "rental")
public record RentalProperties(
        @Min(1) int minStayDays,
        @Min(1) int longTermMinDays,
        @NotNull @DecimalMin("0.00") @DecimalMax(value = "1.00", inclusive = false)
        BigDecimal longTermDiscountRate,
        @Min(1) int maxStayDays,
        @Min(1) int bookingStartMonthsAhead,
        @Min(1) int maxActiveBookingsPerCustomer,
        @NotNull ZoneId zoneId
) {

    public RentalProperties {
        if (longTermMinDays < minStayDays) {
            throw new IllegalArgumentException(
                    "rental.long-term-min-days must not be less than rental.min-stay-days"
            );
        }
        if (maxStayDays < longTermMinDays) {
            throw new IllegalArgumentException(
                    "rental.max-stay-days must not be less than rental.long-term-min-days"
            );
        }
    }
}
