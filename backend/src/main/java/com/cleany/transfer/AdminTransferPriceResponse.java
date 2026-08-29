package com.cleany.transfer;

import java.math.BigDecimal;
import java.time.Instant;

public record AdminTransferPriceResponse(
        long id,
        long airportId,
        String airportCode,
        long vehicleTypeId,
        String vehicleCode,
        TransferDirection direction,
        BigDecimal amount,
        String currency,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt,
        long version
) {

    static AdminTransferPriceResponse from(TransferPrice price) {
        return new AdminTransferPriceResponse(
                price.getId(), price.getAirport().getId(), price.getAirport().getCode(),
                price.getVehicleType().getId(), price.getVehicleType().getCode(),
                price.getDirection(), price.getAmount(), price.getCurrency(), price.isEnabled(),
                price.getCreatedAt(), price.getUpdatedAt(), price.getVersion()
        );
    }
}
