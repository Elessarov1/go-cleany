package com.cleany.transfer;

public record TransferBookingCustomerEvent(
        long bookingId,
        long customerId,
        long communicationIdentityId
) {
}
