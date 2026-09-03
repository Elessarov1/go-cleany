package com.cleany.rental;

record RentalPropertyMediaChangedEvent(long propertyId) {

    RentalPropertyMediaChangedEvent {
        if (propertyId <= 0) {
            throw new IllegalArgumentException("propertyId must be positive");
        }
    }
}
