package com.cleany.crossservice.rentaltransfer;

import java.util.List;

public record RentalTransferContextResponse(
        long rentalBookingId,
        boolean transferFlowAvailable,
        List<RentalTransferContextOptionResponse> options
) {
}
