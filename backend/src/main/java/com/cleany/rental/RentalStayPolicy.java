package com.cleany.rental;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RentalStayPolicy {

    private final RentalProperties properties;
    private final Clock clock;

    public int validate(LocalDate checkInDate, LocalDate checkOutDate) {
        LocalDate checkIn = Objects.requireNonNull(checkInDate, "checkInDate");
        LocalDate checkOut = Objects.requireNonNull(checkOutDate, "checkOutDate");
        LocalDate today = today();
        if (checkIn.isBefore(today) || !checkOut.isAfter(checkIn)) {
            throw new InvalidRentalDateRangeException();
        }

        long durationDays = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (durationDays < properties.minStayDays()) {
            throw new RentalMinimumStayNotMetException(properties.minStayDays());
        }
        if (durationDays > properties.maxStayDays()) {
            throw new RentalMaximumStayExceededException(properties.maxStayDays());
        }
        if (checkIn.isAfter(today.plusMonths(properties.bookingStartMonthsAhead()))) {
            throw new RentalBookingHorizonExceededException(
                    today.plusMonths(properties.bookingStartMonthsAhead())
            );
        }
        return Math.toIntExact(durationDays);
    }

    public LocalDate today() {
        return LocalDate.now(clock.withZone(properties.zoneId()));
    }
}
