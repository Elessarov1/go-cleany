package com.cleany.transfer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateTransferAirportRequest(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9]{3,8}") String code,
        @NotBlank @Size(max = 255) String nameRu,
        @NotBlank @Size(max = 255) String nameEn,
        boolean enabled,
        @Min(0) int sortOrder
) {
}
