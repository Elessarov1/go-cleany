package com.cleany.rental;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.cleany.notification.CustomerNotification;

public sealed interface RentalBookingCustomerNotification extends CustomerNotification {

    long bookingId();

    String titleRu();

    String titleEn();

    LocalDate checkInDate();

    LocalDate checkOutDate();

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
