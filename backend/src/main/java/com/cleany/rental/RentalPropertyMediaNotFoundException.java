package com.cleany.rental;

public class RentalPropertyMediaNotFoundException extends RuntimeException {

    public RentalPropertyMediaNotFoundException(long propertyId, long mediaId) {
        super("Rental property media %d not found for property %d".formatted(mediaId, propertyId));
    }
}
