package com.cleany.transfer;

public record TransferRepeatPrefillResponse(
        long sourceBookingId,
        TransferDirection direction,
        Long airportId,
        Long vehicleTypeId,
        String address,
        int passengerCount,
        int luggageCount
) {
}
