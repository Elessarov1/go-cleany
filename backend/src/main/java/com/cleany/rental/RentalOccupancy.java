package com.cleany.rental;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public record RentalOccupancy(
        long id,
        long propertyId,
        LocalDate startDate,
        LocalDate endDate,
        RentalOccupancyType type,
        Long bookingId,
        String note,
        Instant createdAt,
        Long createdByAdminId
) {

    public RentalOccupancy {
        if (id <= 0 || propertyId <= 0) {
            throw new IllegalArgumentException("Occupancy and property ids must be positive");
        }
        startDate = Objects.requireNonNull(startDate, "startDate");
        endDate = Objects.requireNonNull(endDate, "endDate");
        type = Objects.requireNonNull(type, "type");
        createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }
}
