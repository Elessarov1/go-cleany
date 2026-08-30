package com.cleany.crossservice.rentaltransfer;

public class RentalTransferContextNotEligibleException extends RuntimeException {

    public RentalTransferContextNotEligibleException(long rentalBookingId, String reason) {
        super("Rental booking " + rentalBookingId + " is not eligible for transfer context: " + reason);
    }
}
