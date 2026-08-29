package com.cleany.analytics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateAcquisitionCampaignRequest(
        @NotBlank
        @Size(max = 60)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
        String publicCode,
        @NotBlank @Size(max = 255) String name,
        @NotNull AcquisitionChannel channel,
        @NotNull AcquisitionMedium medium,
        @NotNull AcquisitionTargetService targetService,
        @Positive Long partnerId
) {
}
