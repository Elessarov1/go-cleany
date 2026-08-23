package com.cleany.rental;

public record RentalBookingAdminEvent(long bookingId, Type type) {

    public RentalBookingAdminEvent {
        if (bookingId <= 0) {
            throw new IllegalArgumentException("bookingId must be positive");
        }
    }

    public enum Type {
        CREATED,
        CANCELLED_BY_CUSTOMER
    }
}
