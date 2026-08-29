package com.cleany.transfer;

public class TransferBookingStateException extends RuntimeException {

    public TransferBookingStateException(long bookingId, String operation) {
        super("Transfer booking " + bookingId + " cannot be " + operation + " in its current state");
    }
}
