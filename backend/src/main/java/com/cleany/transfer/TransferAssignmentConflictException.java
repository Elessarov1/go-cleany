package com.cleany.transfer;

public class TransferAssignmentConflictException extends RuntimeException {

    public TransferAssignmentConflictException(long bookingId) {
        super("Transfer booking is no longer available for assignment: " + bookingId);
    }
}
