package com.cleany.rental;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpsertRentalOccupancyRequest(
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull RentalOccupancyType type,
        @Size(max = 1000) String note
) {
}
