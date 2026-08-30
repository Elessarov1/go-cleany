package com.cleany.crossservice.rentaltransfer;

import java.time.LocalDate;

import com.cleany.rental.RentalBooking;
import com.cleany.transfer.TransferDirection;

public enum RentalTransferContextType {
    ARRIVAL(TransferDirection.FROM_AIRPORT) {
        @Override
        public LocalDate suggestedDate(RentalBooking booking) {
            return booking.getCheckInDate();
        }
    },
    CHECKOUT(TransferDirection.TO_AIRPORT) {
        @Override
        public LocalDate suggestedDate(RentalBooking booking) {
            return booking.getCheckOutDate();
        }
    };

    private final TransferDirection direction;

    RentalTransferContextType(TransferDirection direction) {
        this.direction = direction;
    }

    public TransferDirection direction() {
        return direction;
    }

    public abstract LocalDate suggestedDate(RentalBooking booking);
}
