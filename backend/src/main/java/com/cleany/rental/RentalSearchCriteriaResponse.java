package com.cleany.rental;

import java.time.LocalDate;

public record RentalSearchCriteriaResponse(
        RentalSearchMode mode,
        RentalTermType termType,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer rentalMonths,
        Integer durationDays,
        Integer guests
) {

    static RentalSearchCriteriaResponse browseAll() {
        return new RentalSearchCriteriaResponse(
                RentalSearchMode.BROWSE_ALL,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    static RentalSearchCriteriaResponse from(ResolvedRentalTerm term, int guests) {
        return new RentalSearchCriteriaResponse(
                RentalSearchMode.valueOf(term.termType().name()),
                term.termType(),
                term.checkInDate(),
                term.checkOutDate(),
                term.rentalMonths(),
                term.durationDays(),
                guests
        );
    }
}
