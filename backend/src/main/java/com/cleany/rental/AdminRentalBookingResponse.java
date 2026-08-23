package com.cleany.rental;

public record AdminRentalBookingResponse(
        long customerId,
        long communicationIdentityId,
        RentalBookingResponse booking
) {

    static AdminRentalBookingResponse from(RentalBooking booking) {
        return new AdminRentalBookingResponse(
                booking.getCustomerId(),
                booking.getCommunicationIdentityId(),
                RentalBookingResponse.from(booking)
        );
    }
}
