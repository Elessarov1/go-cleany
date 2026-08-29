package com.cleany.transfer;

import java.time.LocalDate;
import java.util.List;

public record TransferConfigurationResponse(
        LocalDate earliestBookingDate,
        LocalDate latestBookingDate,
        int timeSlotMinutes,
        List<TransferAirportResponse> airports,
        List<TransferVehicleTypeResponse> vehicleTypes,
        List<TransferPriceResponse> prices
) {
}
