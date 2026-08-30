package com.cleany.crossservice.rentaltransfer;

import java.time.LocalDate;

import com.cleany.transfer.TransferDirection;

public record RentalTransferContextOptionResponse(
        RentalTransferContextType context,
        RentalTransferContextAvailability availability,
        TransferDirection direction,
        LocalDate suggestedDate,
        String address,
        LocalDate availableFromDate
) {
}
