package com.cleany.rental;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.cleany.notification.CustomerNotification;
import com.cleany.notification.CustomerNotificationType;

public sealed interface RentalBookingCustomerNotification extends CustomerNotification {

    long bookingId();

    String titleRu();

    String titleEn();

    LocalDate checkInDate();

    LocalDate checkOutDate();

    @Override
    default CustomerNotificationType type() {
        return this instanceof Confirmed
                ? CustomerNotificationType.RENTAL_BOOKING_CONFIRMED
                : CustomerNotificationType.RENTAL_BOOKING_CANCELLED;
    }

    @Override
    default String targetPath() {
        return "/rent/bookings/" + bookingId();
    }

    @Override
    default String deduplicationKey() {
        return "rental-booking:" + bookingId() + ":"
                + (this instanceof Confirmed ? "confirmed" : "cancelled");
    }

    record Confirmed(
            long bookingId,
            String titleRu,
            String titleEn,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            BigDecimal totalPrice,
            String currency
    ) implements RentalBookingCustomerNotification {
    }

    record Cancelled(
            long bookingId,
            String titleRu,
            String titleEn,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            RentalBookingStatus status
    ) implements RentalBookingCustomerNotification {
    }
}
