package com.cleany.crossservice.rentaltransfer;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "rental-transfer-benefit")
public record RentalTransferBenefitProperties(
        boolean enabled,
        @NotNull BigDecimal discountRate
) {

    public RentalTransferBenefitProperties {
        if (enabled && (discountRate.signum() <= 0 || discountRate.compareTo(BigDecimal.ONE) >= 0)) {
            throw new IllegalArgumentException(
                    "Rental transfer benefit discount rate must be greater than 0 and less than 1"
            );
        }
    }
}
