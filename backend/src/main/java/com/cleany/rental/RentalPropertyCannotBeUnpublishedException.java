package com.cleany.rental;

public class RentalPropertyCannotBeUnpublishedException extends RuntimeException {

    public RentalPropertyCannotBeUnpublishedException(long propertyId) {
        super("Rental property " + propertyId + " is not published");
    }
}
