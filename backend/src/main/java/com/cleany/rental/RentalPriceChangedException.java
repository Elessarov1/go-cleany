package com.cleany.rental;

public class RentalPriceChangedException extends RuntimeException {

    public RentalPriceChangedException() {
        super("Rental price has changed since it was last confirmed");
    }
}
