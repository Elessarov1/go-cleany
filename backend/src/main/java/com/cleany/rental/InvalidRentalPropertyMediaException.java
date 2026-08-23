package com.cleany.rental;

public class InvalidRentalPropertyMediaException extends RuntimeException {

    public InvalidRentalPropertyMediaException(String message) {
        super(message);
    }

    public InvalidRentalPropertyMediaException(String message, Throwable cause) {
        super(message, cause);
    }
}
