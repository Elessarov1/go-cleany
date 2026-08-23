package com.cleany.rental;

public class InvalidRentalBookingException extends RuntimeException {

    public InvalidRentalBookingException(String message) {
        super(message);
    }
}
