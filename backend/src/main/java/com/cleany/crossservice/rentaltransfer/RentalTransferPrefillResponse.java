package com.cleany.crossservice.rentaltransfer;

import java.time.LocalDate;

import com.cleany.transfer.TransferDirection;

public record RentalTransferPrefillResponse(
        long rentalBookingId,
        RentalTransferContextType context,
        TransferDirection direction,
        LocalDate suggestedDate,
        String address
) {
}
