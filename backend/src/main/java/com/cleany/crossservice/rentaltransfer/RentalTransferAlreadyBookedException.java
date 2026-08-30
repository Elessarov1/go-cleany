package com.cleany.crossservice.rentaltransfer;

public class RentalTransferAlreadyBookedException extends RuntimeException {

    public RentalTransferAlreadyBookedException(long rentalBookingId, RentalTransferContextType context) {
        super("A matching transfer already exists for rental booking "
                + rentalBookingId + " and context " + context);
    }
}
