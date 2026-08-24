package com.cleany.order;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CleaningOrderQuoteRequest(
        @NotNull ApartmentType apartmentType,
        boolean duplex,
        @NotNull CleaningType cleaningType,
        @Size(max = 32) String referralCode,
        LocalDate requestedDate,
        @Size(max = 32) String rentalCleaningPromoCode
) {

    public CleaningOrderQuoteRequest(
            ApartmentType apartmentType,
            boolean duplex,
            CleaningType cleaningType,
            String referralCode
    ) {
        this(apartmentType, duplex, cleaningType, referralCode, null, null);
    }
}
