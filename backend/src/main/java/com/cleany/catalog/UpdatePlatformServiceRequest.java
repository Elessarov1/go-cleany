package com.cleany.catalog;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdatePlatformServiceRequest(
        PlatformServiceStatus status,
        @Min(0) @Max(9999) Integer displayOrder
) {

    @AssertTrue(message = "status or displayOrder must be provided")
    public boolean isAnySettingProvided() {
        return status != null || displayOrder != null;
    }
}
