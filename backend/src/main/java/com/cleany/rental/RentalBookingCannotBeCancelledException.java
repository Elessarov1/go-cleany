package com.cleany.rental;

public class RentalBookingCannotBeCancelledException extends RuntimeException {

    public RentalBookingCannotBeCancelledException(long bookingId) {
        super("Rental booking cannot be cancelled: " + bookingId);
    }
}
