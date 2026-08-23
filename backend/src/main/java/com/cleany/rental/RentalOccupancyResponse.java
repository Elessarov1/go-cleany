package com.cleany.rental;

import java.time.Instant;
import java.time.LocalDate;

public record RentalOccupancyResponse(
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

    static RentalOccupancyResponse from(RentalOccupancy occupancy) {
        return new RentalOccupancyResponse(
                occupancy.id(),
                occupancy.propertyId(),
                occupancy.startDate(),
                occupancy.endDate(),
                occupancy.type(),
                occupancy.bookingId(),
                occupancy.note(),
                occupancy.createdAt(),
                occupancy.createdByAdminId()
        );
    }
}
