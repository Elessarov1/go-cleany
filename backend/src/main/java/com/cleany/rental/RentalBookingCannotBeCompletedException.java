package com.cleany.rental;

public class RentalBookingCannotBeCompletedException extends RuntimeException {

    public RentalBookingCannotBeCompletedException(long bookingId) {
        super("Rental booking cannot be completed: " + bookingId);
    }
}
