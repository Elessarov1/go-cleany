package com.cleany.rental;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RentalStayPolicyTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 8, 23);

    private RentalStayPolicy policy;

    @BeforeEach
    void setUp() {
        var properties = new RentalProperties(
                7,
                30,
                new BigDecimal("0.10"),
                365,
                6,
                3,
                ZoneId.of("Europe/Istanbul")
        );
        policy = new RentalStayPolicy(
                properties,
                Clock.fixed(TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        );
    }

    @Test
    void minimumAndMaximumStay_boundariesEnforced() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        RentalMinimumStayNotMetException.class,
                        () -> policy.validate(TODAY, TODAY.plusDays(6))
                ),
                () -> Assertions.assertEquals(7, policy.validate(TODAY, TODAY.plusDays(7))),
                () -> Assertions.assertEquals(365, policy.validate(TODAY, TODAY.plusDays(365))),
                () -> Assertions.assertThrows(
                        RentalMaximumStayExceededException.class,
                        () -> policy.validate(TODAY, TODAY.plusDays(366))
                )
        );
    }

    @Test
    void bookingHorizon_usesCalendarMonthsAndDoesNotRestrictCheckout() {
        LocalDate lastCheckIn = TODAY.plusMonths(6);

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        45,
                        policy.validate(lastCheckIn, lastCheckIn.plusDays(45))
                ),
                () -> Assertions.assertThrows(
                        RentalBookingHorizonExceededException.class,
                        () -> policy.validate(lastCheckIn.plusDays(1), lastCheckIn.plusDays(31))
                )
        );
    }

    @Test
    void pastOrReversedRange_rejected() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        InvalidRentalDateRangeException.class,
                        () -> policy.validate(TODAY.minusDays(1), TODAY.plusDays(7))
                ),
                () -> Assertions.assertThrows(
                        InvalidRentalDateRangeException.class,
                        () -> policy.validate(TODAY, TODAY)
                )
        );
    }
}
