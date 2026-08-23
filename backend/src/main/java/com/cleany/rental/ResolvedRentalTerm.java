package com.cleany.rental;

import java.time.LocalDate;
import java.util.Objects;

public record ResolvedRentalTerm(
        RentalTermType termType,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int durationDays,
        Integer rentalMonths
) {

    public ResolvedRentalTerm {
        termType = Objects.requireNonNull(termType, "termType");
        checkInDate = Objects.requireNonNull(checkInDate, "checkInDate");
        checkOutDate = Objects.requireNonNull(checkOutDate, "checkOutDate");
        if (durationDays <= 0) {
            throw new IllegalArgumentException("durationDays must be positive");
        }
        if (termType == RentalTermType.DATE_RANGE && rentalMonths != null) {
            throw new IllegalArgumentException("DATE_RANGE must not contain rentalMonths");
        }
        if (termType == RentalTermType.MONTHLY
                && (rentalMonths == null || rentalMonths <= 0)) {
            throw new IllegalArgumentException("MONTHLY requires positive rentalMonths");
        }
    }
}
