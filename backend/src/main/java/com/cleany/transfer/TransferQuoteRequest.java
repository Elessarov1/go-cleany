package com.cleany.transfer;

import com.cleany.crossservice.rentaltransfer.RentalTransferSourceRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TransferQuoteRequest(
        @NotNull TransferDirection direction,
        @Min(1) long airportId,
        @Min(1) long vehicleTypeId,
        @Valid RentalTransferSourceRequest rentalSource,
        TransferBenefitType benefit
) {
}
