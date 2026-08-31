package com.cleany.support;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.cleany.catalog.PlatformService;

public record CreateSupportCaseRequest(
        @NotNull PlatformService service,
        @Positive long sourceEntityId,
        @NotNull SupportCaseCategory category,
        @Size(max = 2000) String description
) {
}
