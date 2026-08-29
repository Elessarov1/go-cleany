package com.cleany.analytics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TelegramAcquisitionCaptureRequest(
        @NotBlank
        @Size(max = 60)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
        String publicCode
) {
}
