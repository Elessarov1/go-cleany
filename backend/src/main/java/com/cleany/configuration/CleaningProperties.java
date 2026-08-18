package com.cleany.configuration;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Currency;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import com.cleany.order.ApartmentType;

@Validated
@ConfigurationProperties(prefix = "cleaning")
public record CleaningProperties(
        @Min(1) @Max(60) int bookingDaysAhead,
        @NotNull Currency currency,
        @NotNull ZoneId zoneId,
        @Valid @NotNull Prices prices
) {

    public record Prices(
            @Valid @NotNull PriceGroup regular,
            @Valid @NotNull PriceGroup deep
    ) {
    }

    public record PriceGroup(
            @NotNull @DecimalMin("0.00") BigDecimal studio,
            @NotNull @DecimalMin("0.00") BigDecimal onePlusOne,
            @NotNull @DecimalMin("0.00") BigDecimal twoPlusOne,
            @NotNull @DecimalMin("0.00") BigDecimal threePlusOne,
            @NotNull @DecimalMin("0.00") BigDecimal fourPlusOne,
            @NotNull @DecimalMin("0.00") BigDecimal duplexSurcharge
    ) {

        public BigDecimal priceFor(ApartmentType apartmentType) {
            return switch (apartmentType) {
                case STUDIO -> studio;
                case ONE_PLUS_ONE -> onePlusOne;
                case TWO_PLUS_ONE -> twoPlusOne;
                case THREE_PLUS_ONE -> threePlusOne;
                case FOUR_PLUS_ONE -> fourPlusOne;
            };
        }
    }
}
