package com.cleany.transfer;

import java.math.BigDecimal;

public record TransferPriceResponse(
        long airportId,
        long vehicleTypeId,
        TransferDirection direction,
        BigDecimal amount,
        String currency
) {

    static TransferPriceResponse from(TransferPrice price) {
        return new TransferPriceResponse(
                price.getAirport().getId(),
                price.getVehicleType().getId(),
                price.getDirection(),
                price.getAmount(),
                price.getCurrency()
        );
    }
}
