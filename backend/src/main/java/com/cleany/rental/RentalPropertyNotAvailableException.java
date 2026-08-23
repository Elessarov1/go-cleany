package com.cleany.rental;

public class RentalPropertyNotAvailableException extends RuntimeException {

    public RentalPropertyNotAvailableException(long propertyId) {
        super("Rental property is not available for booking: " + propertyId);
    }
}
