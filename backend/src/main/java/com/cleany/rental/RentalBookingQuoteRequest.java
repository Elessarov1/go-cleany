package com.cleany.rental;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RentalBookingQuoteRequest(
        @Positive long propertyId,
        @NotNull RentalTermType termType,
        @NotNull LocalDate checkInDate,
        LocalDate checkOutDate,
        @Positive Integer months
) {
}
