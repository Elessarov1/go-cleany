package com.cleany.rental;

import jakarta.validation.constraints.Size;

public record AdminCancelRentalBookingRequest(
        @Size(max = 1000) String reason,
        boolean keepDatesUnavailable
) {
}
