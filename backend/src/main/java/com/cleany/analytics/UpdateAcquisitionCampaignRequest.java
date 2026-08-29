package com.cleany.analytics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateAcquisitionCampaignRequest(
        @NotBlank @Size(max = 255) String name,
        @NotNull AcquisitionChannel channel,
        @NotNull AcquisitionMedium medium,
        @NotNull AcquisitionTargetService targetService,
        @Positive Long partnerId,
        @NotNull Boolean active
) {
}
