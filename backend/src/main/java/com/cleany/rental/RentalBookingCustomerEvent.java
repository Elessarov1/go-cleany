package com.cleany.rental;

public sealed interface RentalBookingCustomerEvent {

    long bookingId();

    long customerId();

    long communicationIdentityId();

    record Confirmed(
            long bookingId,
            long customerId,
            long communicationIdentityId
    ) implements RentalBookingCustomerEvent {
    }

    record Cancelled(
            long bookingId,
            long customerId,
            long communicationIdentityId,
            RentalBookingStatus status
    ) implements RentalBookingCustomerEvent {
    }
}
