package com.cleany.rental;

public class RentalPropertyNotFoundException extends RuntimeException {

    public RentalPropertyNotFoundException(long propertyId) {
        super("Rental property not found: " + propertyId);
    }

    public RentalPropertyNotFoundException(String slug) {
        super("Rental property not found: " + slug);
    }
}
