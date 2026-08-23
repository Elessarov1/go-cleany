package com.cleany.rental;

import java.time.LocalDate;

public record RentalAvailabilityRangeResponse(
        LocalDate startDate,
        LocalDate endDate
) {

    static RentalAvailabilityRangeResponse from(RentalOccupancy occupancy) {
        return new RentalAvailabilityRangeResponse(occupancy.startDate(), occupancy.endDate());
    }
}
