package com.cleany.transfer;

public class TransferBookingNotFoundException extends RuntimeException {

    public TransferBookingNotFoundException(long bookingId) {
        super("Transfer booking not found: " + bookingId);
    }
}
