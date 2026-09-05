package com.cleany.crossservice.rentaltransfer;

public class RentalTransferBenefitUnavailableException extends RuntimeException {

    public RentalTransferBenefitUnavailableException(long rentalBookingId) {
        super("Rental transfer benefit is no longer available for booking " + rentalBookingId);
    }
}
