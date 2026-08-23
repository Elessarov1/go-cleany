package com.cleany.rental;

public class RentalPropertySlugConflictException extends RuntimeException {

    public RentalPropertySlugConflictException(String slug) {
        super("Rental property slug is already in use: " + slug);
    }
}
