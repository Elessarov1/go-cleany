package com.cleany.rental;

public class RentalDatesNotAvailableException extends RuntimeException {

    public RentalDatesNotAvailableException() {
        super("The selected rental dates are not available");
    }
}
