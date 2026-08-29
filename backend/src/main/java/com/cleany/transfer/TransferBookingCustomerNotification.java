package com.cleany.transfer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.cleany.notification.CustomerNotification;
import com.cleany.notification.CustomerNotificationType;

public record TransferBookingCustomerNotification(
        long bookingId,
        TransferBookingCustomerNotification.Type eventType,
        TransferDirection direction,
        String airportCode,
        String vehicleNameRu,
        String vehicleNameEn,
        LocalDate pickupDate,
        LocalTime pickupTime,
        BigDecimal priceAmount,
        String priceCurrency
) implements CustomerNotification {

    public enum Type {
        REQUESTED,
        CONFIRMED,
        REJECTED,
        CANCELLED,
        COMPLETED
    }

    @Override
    public CustomerNotificationType type() {
        return switch (eventType) {
            case REQUESTED -> CustomerNotificationType.TRANSFER_REQUESTED;
            case CONFIRMED -> CustomerNotificationType.TRANSFER_CONFIRMED;
            case REJECTED -> CustomerNotificationType.TRANSFER_REJECTED;
            case CANCELLED -> CustomerNotificationType.TRANSFER_CANCELLED;
            case COMPLETED -> CustomerNotificationType.TRANSFER_COMPLETED;
        };
    }

    @Override
    public String targetPath() {
        return "/transfer/bookings/" + bookingId;
    }

    @Override
    public String deduplicationKey() {
        return "transfer-booking:" + bookingId + ":" + eventType.name().toLowerCase();
    }
}
