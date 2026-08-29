package com.cleany.transfer;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Objects;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransferBookingPolicy {

    private final TransferProperties properties;
    private final Clock clock;

    public void requireBookable(LocalDate pickupDate, LocalTime pickupTime) {
        LocalDate requiredDate = Objects.requireNonNull(pickupDate, "pickupDate");
        LocalTime requiredTime = Objects.requireNonNull(pickupTime, "pickupTime");
        LocalDate earliest = earliestBookingDate();
        LocalDate latest = latestBookingDate();
        if (requiredDate.isBefore(earliest) || requiredDate.isAfter(latest)) {
            throw new InvalidTransferBookingException(
                    "Pickup date must be between " + earliest + " and " + latest
            );
        }
        if (requiredTime.getSecond() != 0
                || requiredTime.getNano() != 0
                || requiredTime.getMinute() % properties.timeSlotMinutes() != 0) {
            throw new InvalidTransferBookingException(
                    "Pickup time must match a " + properties.timeSlotMinutes() + " minute slot"
            );
        }
    }

    public LocalDate earliestBookingDate() {
        return today().plusDays(properties.minBookingDaysAhead());
    }

    public LocalDate latestBookingDate() {
        return today().plusMonths(properties.bookingMonthsAhead());
    }

    public int timeSlotMinutes() {
        return properties.timeSlotMinutes();
    }

    public boolean hasStarted(TransferBooking booking) {
        Objects.requireNonNull(booking, "booking");
        return !clock.instant().isBefore(pickupInstant(booking.getPickupDate(), booking.getPickupTime()));
    }

    public Instant pickupInstant(LocalDate pickupDate, LocalTime pickupTime) {
        return ZonedDateTime.of(pickupDate, pickupTime, properties.zoneId()).toInstant();
    }

    private LocalDate today() {
        return LocalDate.now(clock.withZone(properties.zoneId()));
    }
}
