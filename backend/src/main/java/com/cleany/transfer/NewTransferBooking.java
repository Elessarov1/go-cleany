package com.cleany.transfer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record NewTransferBooking(
        long customerId,
        long communicationIdentityId,
        String customerName,
        String customerPhone,
        TransferDirection direction,
        TransferAirport airport,
        TransferVehicleType vehicleType,
        LocalDate pickupDate,
        LocalTime pickupTime,
        String address,
        int passengerCount,
        int luggageCount,
        String flightNumber,
        LocalTime scheduledArrivalTime,
        String comment,
        TransferPrice price,
        TransferPriceQuote priceQuote,
        Instant createdAt
) {
}
