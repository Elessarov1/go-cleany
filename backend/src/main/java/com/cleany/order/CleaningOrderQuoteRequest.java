package com.cleany.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CleaningOrderQuoteRequest(
        @NotNull ApartmentType apartmentType,
        boolean duplex,
        @NotNull CleaningType cleaningType,
        @Size(max = 32) String referralCode
) {
}
