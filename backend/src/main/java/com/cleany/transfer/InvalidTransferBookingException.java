package com.cleany.transfer;

public class InvalidTransferBookingException extends RuntimeException {

    public InvalidTransferBookingException(String message) {
        super(message);
    }
}
