package com.cleany.rental;

public class RentalPropertyCannotBePublishedException extends RuntimeException {

    public RentalPropertyCannotBePublishedException(String message) {
        super(message);
    }
}
