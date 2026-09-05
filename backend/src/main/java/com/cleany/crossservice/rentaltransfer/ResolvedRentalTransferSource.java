package com.cleany.crossservice.rentaltransfer;

import com.cleany.rental.RentalBooking;

public record ResolvedRentalTransferSource(
        long rentalBookingId,
        RentalTransferContextType context,
        RentalBooking booking
) {
}
