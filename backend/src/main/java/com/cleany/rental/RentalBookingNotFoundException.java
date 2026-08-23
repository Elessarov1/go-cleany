package com.cleany.rental;

public class RentalBookingNotFoundException extends RuntimeException {

    public RentalBookingNotFoundException(long bookingId) {
        super("Rental booking not found: " + bookingId);
    }
}
