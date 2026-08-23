package com.cleany.rental;

public class InvalidRentalDateRangeException extends RuntimeException {

    public InvalidRentalDateRangeException() {
        super("Rental check-in must not be in the past and check-out must be after check-in");
    }
}
