package com.cleany.crossservice.rentaltransfer;

public record ResolvedRentalTransferSource(
        long rentalBookingId,
        RentalTransferContextType context
) {
}
