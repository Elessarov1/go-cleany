package com.cleany.finance;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "referral.financial")
public record ReferralFinancialProperties(
        @NotNull @DecimalMin("0.00") @DecimalMax("1.00") BigDecimal commissionRate,
        @Valid @NotNull Customer customer,
        @Valid @NotNull Partner partner
) {

    public record Customer(
            @NotNull @DecimalMin("0.00") @DecimalMax("1.00") BigDecimal friendDiscountRate,
            @NotNull @DecimalMin("0.00") BigDecimal friendMaxDiscount,
            @NotNull @DecimalMin("0.00") @DecimalMax("1.00") BigDecimal referrerRewardRate,
            @NotNull @DecimalMin("0.00") BigDecimal referrerMaxDiscount
    ) {
    }

    public record Partner(
            @NotNull @DecimalMin("0.00") @DecimalMax("1.00") BigDecimal customerDiscountRate,
            @NotNull @DecimalMin("0.00") BigDecimal customerMaxDiscount,
            @NotNull @DecimalMin("0.00") @DecimalMax("1.00") BigDecimal payoutRate,
            @NotNull @DecimalMin("0.00") BigDecimal maxPayout
    ) {
    }
}
