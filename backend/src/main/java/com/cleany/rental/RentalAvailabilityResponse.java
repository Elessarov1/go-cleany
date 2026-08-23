package com.cleany.rental;

import java.time.LocalDate;
import java.util.List;

public record RentalAvailabilityResponse(
        long propertyId,
        LocalDate fromDate,
        LocalDate toDate,
        List<RentalAvailabilityRangeResponse> unavailableRanges
) {

    public RentalAvailabilityResponse {
        unavailableRanges = List.copyOf(unavailableRanges);
    }
}
