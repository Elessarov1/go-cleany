package com.cleany.rental;

public class RentalPropertyCannotBeDeletedException extends RuntimeException {

    public RentalPropertyCannotBeDeletedException(long propertyId, String reason) {
        super("Rental property " + propertyId + " cannot be deleted: " + reason);
    }
}
