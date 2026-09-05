package com.cleany.transfer;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record TransferBookingResponse(
        long id,
        TransferDirection direction,
        String airportCode,
        String airportNameRu,
        String airportNameEn,
        String vehicleCode,
        String vehicleNameRu,
        String vehicleNameEn,
        LocalDate pickupDate,
        LocalTime pickupTime,
        String address,
        int passengerCount,
        int luggageCount,
        String flightNumber,
        LocalTime scheduledArrivalTime,
        String customerName,
        String phone,
        String comment,
        BigDecimal basePriceAmount,
        BigDecimal discountAmount,
        BigDecimal priceAmount,
        String priceCurrency,
        TransferBenefitType appliedBenefit,
        BigDecimal benefitRate,
        TransferBookingStatus status,
        Long driverId,
        String driverName,
        Instant createdAt,
        Instant confirmedAt,
        Instant completedAt,
        Instant cancelledAt,
        Instant rejectedAt,
        String statusReason
) {

    static TransferBookingResponse from(TransferBooking booking) {
        TransferDriver driver = booking.getDriver();
        return new TransferBookingResponse(
                booking.getId(),
                booking.getDirection(),
                booking.getAirportCodeSnapshot(),
                booking.getAirportNameRuSnapshot(),
                booking.getAirportNameEnSnapshot(),
                booking.getVehicleCodeSnapshot(),
                booking.getVehicleNameRuSnapshot(),
                booking.getVehicleNameEnSnapshot(),
                booking.getPickupDate(),
                booking.getPickupTime(),
                booking.getAddress(),
                booking.getPassengerCount(),
                booking.getLuggageCount(),
                booking.getFlightNumber(),
                booking.getScheduledArrivalTime(),
                booking.getCustomerNameSnapshot(),
                booking.getCustomerPhoneSnapshot(),
                booking.getComment(),
                booking.getBasePriceAmount(),
                booking.getDiscountAmount(),
                booking.getPriceAmount(),
                booking.getPriceCurrency(),
                booking.getAppliedBenefit(),
                booking.getBenefitRate(),
                booking.getStatus(),
                driver == null ? null : driver.getId(),
                driver == null ? null : driver.getName(),
                booking.getCreatedAt(),
                booking.getConfirmedAt(),
                booking.getCompletedAt(),
                booking.getCancelledAt(),
                booking.getRejectedAt(),
                booking.getStatusReason()
        );
    }
}
