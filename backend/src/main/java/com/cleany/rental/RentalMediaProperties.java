package com.cleany.rental;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("rental-media")
public record RentalMediaProperties(
        @DefaultValue("true") boolean backfillEnabled,
        @DefaultValue("10") @Min(1) int backfillBatchSize,
        @DefaultValue("true") boolean cacheEnabled,
        @DefaultValue("64MB") @NotNull DataSize cacheMaxSize
) {

    @AssertTrue(message = "rental-media.cache-max-size must be positive")
    public boolean isCacheMaxSizePositive() {
        return cacheMaxSize != null && cacheMaxSize.toBytes() > 0;
    }
}
