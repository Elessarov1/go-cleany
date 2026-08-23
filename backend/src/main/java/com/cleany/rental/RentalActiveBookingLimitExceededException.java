package com.cleany.rental;

public class RentalActiveBookingLimitExceededException extends RuntimeException {

    public RentalActiveBookingLimitExceededException(int maximumActiveBookings) {
        super("Customer already has the maximum of " + maximumActiveBookings + " active bookings");
    }
}
