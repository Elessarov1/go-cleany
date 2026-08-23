package com.cleany.rental;

public class RentalMaximumStayExceededException extends RuntimeException {

    public RentalMaximumStayExceededException(int maximumDays) {
        super("Rental stay must not exceed " + maximumDays + " days");
    }
}
