package com.cleany.rental;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RentalStayPolicy {

    private final RentalProperties properties;
    private final Clock clock;

    public ResolvedRentalTerm resolve(
            RentalTermType termType,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Integer months
    ) {
        if (termType == null || checkInDate == null) {
            throw new InvalidRentalBookingException(
                    "Rental term type and check-in date are required"
            );
        }
        return switch (termType) {
            case DATE_RANGE -> resolveDateRange(checkInDate, checkOutDate, months);
            case MONTHLY -> resolveMonthly(checkInDate, checkOutDate, months);
        };
    }

    private ResolvedRentalTerm resolveDateRange(
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Integer months
    ) {
        if (months != null || checkOutDate == null) {
            throw new InvalidRentalBookingException(
                    "DATE_RANGE requires checkOutDate and must not contain months"
            );
        }
        validateStartDate(checkInDate);
        int durationDays = durationDays(checkInDate, checkOutDate);
        if (durationDays < properties.minStayDays()) {
            throw new RentalMinimumStayNotMetException(properties.minStayDays());
        }
        int maximumDateRangeDays = properties.longTermMinDays() - 1;
        if (durationDays > maximumDateRangeDays) {
            throw new RentalMaximumStayExceededException(maximumDateRangeDays);
        }
        return new ResolvedRentalTerm(
                RentalTermType.DATE_RANGE,
                checkInDate,
                checkOutDate,
                durationDays,
                null
        );
    }

    private ResolvedRentalTerm resolveMonthly(
            LocalDate checkInDate,
            LocalDate clientCheckOutDate,
            Integer months
    ) {
        if (clientCheckOutDate != null || months == null || months <= 0) {
            throw new InvalidRentalBookingException(
                    "MONTHLY requires positive months and must not contain checkOutDate"
            );
        }
        validateStartDate(checkInDate);
        LocalDate checkOut;
        try {
            checkOut = checkInDate.plusMonths(months);
        } catch (RuntimeException exception) {
            throw new InvalidRentalBookingException("MONTHLY duration is invalid");
        }
        int durationDays = durationDays(checkInDate, checkOut);
        if (durationDays > properties.maxStayDays()) {
            throw new RentalMaximumStayExceededException(properties.maxStayDays());
        }
        return new ResolvedRentalTerm(
                RentalTermType.MONTHLY,
                checkInDate,
                checkOut,
                durationDays,
                months
        );
    }

    private void validateStartDate(LocalDate checkIn) {
        LocalDate today = today();
        if (checkIn.isBefore(today)) {
            throw new InvalidRentalDateRangeException();
        }
        LocalDate lastCheckInDate = today.plusMonths(properties.bookingStartMonthsAhead());
        if (checkIn.isAfter(lastCheckInDate)) {
            throw new RentalBookingHorizonExceededException(lastCheckInDate);
        }
    }

    private static int durationDays(LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidRentalDateRangeException();
        }
        return Math.toIntExact(ChronoUnit.DAYS.between(checkIn, checkOut));
    }

    public LocalDate today() {
        return LocalDate.now(clock.withZone(properties.zoneId()));
    }
}
