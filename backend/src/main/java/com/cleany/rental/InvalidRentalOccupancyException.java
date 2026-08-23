package com.cleany.rental;

public class InvalidRentalOccupancyException extends RuntimeException {

    public InvalidRentalOccupancyException(String message) {
        super(message);
    }
}
