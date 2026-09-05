package com.cleany.rental;

public class InvalidRentalPropertyOrderException extends RuntimeException {

    public InvalidRentalPropertyOrderException() {
        super("Rental property order must contain every property exactly once");
    }
}
