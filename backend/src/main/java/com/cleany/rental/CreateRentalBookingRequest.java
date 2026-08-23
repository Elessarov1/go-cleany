package com.cleany.rental;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateRentalBookingRequest(
        @Positive long propertyId,
        @NotNull LocalDate checkInDate,
        @NotNull LocalDate checkOutDate,
        @Positive @Max(100) int guests,
        @NotBlank @Size(max = 40) String phone,
        @Size(max = 1000) String comment
) {
}
