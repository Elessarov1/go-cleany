package com.cleany.rental;

public enum RentalOccupancyType {
    BOOKING,
    OWNER_BLOCK,
    EXTERNAL_BOOKING,
    MAINTENANCE;

    public boolean manuallyManaged() {
        return this != BOOKING;
    }
}
