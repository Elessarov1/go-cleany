package com.cleany.transfer;

public record TransferBookingCreatedEvent(
        TransferBookingResponse booking,
        long customerId,
        long communicationIdentityId
) {
}
