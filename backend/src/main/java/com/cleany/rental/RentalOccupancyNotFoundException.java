package com.cleany.rental;

public class RentalOccupancyNotFoundException extends RuntimeException {

    public RentalOccupancyNotFoundException(long occupancyId) {
        super("Rental occupancy not found: " + occupancyId);
    }
}
