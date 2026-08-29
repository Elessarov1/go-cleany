package com.cleany.transfer;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTransferBookingRequest(
        @NotNull TransferDirection direction,
        @Min(1) long airportId,
        @Min(1) long vehicleTypeId,
        @NotNull LocalDate pickupDate,
        @NotNull LocalTime pickupTime,
        @NotBlank @Size(max = 1000) String address,
        @Min(1) @Max(50) int passengerCount,
        @Min(0) @Max(50) int luggageCount,
        @Size(max = 64) String flightNumber,
        LocalTime scheduledArrivalTime,
        @NotBlank @Size(max = 40) String phone,
        @Size(max = 1000) String comment
) {
}
