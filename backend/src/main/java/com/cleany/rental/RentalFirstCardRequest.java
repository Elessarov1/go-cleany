package com.cleany.rental;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;

public record RentalFirstCardRequest(
        @PositiveOrZero @Max(600_000) long durationMs
) {
}
