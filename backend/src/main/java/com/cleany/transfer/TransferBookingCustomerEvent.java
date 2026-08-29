package com.cleany.transfer;

public record TransferBookingCustomerEvent(
        long bookingId,
        long customerId,
        long communicationIdentityId,
        Type type
) {

    public enum Type {
        CONFIRMED,
        REJECTED,
        CANCELLED,
        COMPLETED
    }
}
