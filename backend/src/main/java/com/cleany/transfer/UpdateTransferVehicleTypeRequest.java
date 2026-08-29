package com.cleany.transfer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTransferVehicleTypeRequest(
        @NotBlank @Size(max = 255) String nameRu,
        @NotBlank @Size(max = 255) String nameEn,
        @Min(1) int maxPassengers,
        @Min(0) int maxLuggage,
        boolean enabled,
        @Min(0) int sortOrder
) {
}
