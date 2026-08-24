package com.cleany.crossservice.rentalcleaning;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "rental-cleaning-benefit")
public record RentalCleaningBenefitProperties(
        boolean issuanceEnabled,
        @NotBlank String issuanceCron,
        @Min(1) int issuanceBatchSize,
        @Min(0) int checkoutWindowDays,
        @NotNull @DecimalMin("0.00") @DecimalMax("1.00") BigDecimal discountRate,
        @NotNull @DecimalMin("0.00") BigDecimal maxDiscount
) {
}
