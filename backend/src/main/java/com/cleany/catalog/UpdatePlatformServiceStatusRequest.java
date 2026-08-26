package com.cleany.catalog;

import jakarta.validation.constraints.NotNull;

public record UpdatePlatformServiceStatusRequest(
        @NotNull PlatformServiceStatus status
) {
}
