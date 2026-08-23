package com.cleany.rental;

import java.time.LocalDate;

public class RentalBookingHorizonExceededException extends RuntimeException {

    public RentalBookingHorizonExceededException(LocalDate lastAllowedCheckInDate) {
        super("Rental check-in must be no later than " + lastAllowedCheckInDate);
    }
}
