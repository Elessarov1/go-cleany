package com.cleany.rental;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

final class RentalDateRange {

    private RentalDateRange() {
    }

    static int inclusiveDuration(LocalDate startDate, LocalDate endDate) {
        requireValid(startDate, endDate);
        return Math.toIntExact(ChronoUnit.DAYS.between(startDate, endDate) + 1);
    }

    static LocalDate exclusiveEnd(LocalDate inclusiveEndDate) {
        if (inclusiveEndDate == null) {
            throw new InvalidRentalDateRangeException();
        }
        try {
            return inclusiveEndDate.plusDays(1);
        } catch (DateTimeException exception) {
            throw new InvalidRentalDateRangeException();
        }
    }

    static LocalDate inclusiveEnd(LocalDate exclusiveEndDate) {
        if (exclusiveEndDate == null) {
            throw new IllegalStateException("Rental occupancy has no upper date bound");
        }
        return exclusiveEndDate.minusDays(1);
    }

    static void requireValid(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new InvalidRentalDateRangeException();
        }
    }
}
