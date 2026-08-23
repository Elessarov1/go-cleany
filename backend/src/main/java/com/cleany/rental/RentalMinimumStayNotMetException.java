package com.cleany.rental;

public class RentalMinimumStayNotMetException extends RuntimeException {

    public RentalMinimumStayNotMetException(int minimumDays) {
        super("Rental stay must be at least " + minimumDays + " days");
    }
}
