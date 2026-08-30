package com.cleany.crossservice.rentaltransfer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RentalTransferSourceRequest(
        @Positive long bookingId,
        @NotNull RentalTransferContextType context
) {
}
