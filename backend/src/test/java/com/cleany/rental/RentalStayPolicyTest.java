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
        policy = policyAt(properties, TODAY);
    }

    @Test
    void dateRange_minimumAndMonthlyThresholdBoundariesEnforced() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        RentalMinimumStayNotMetException.class,
                        () -> policy.resolve(
                                RentalTermType.DATE_RANGE,
                                TODAY,
                                TODAY.plusDays(6),
                                null
                        )
                ),
                () -> Assertions.assertEquals(
                        7,
                        policy.resolve(
                                RentalTermType.DATE_RANGE,
                                TODAY,
                                TODAY.plusDays(7),
                                null
                        ).durationDays()
                ),
                () -> Assertions.assertEquals(
                        29,
                        policy.resolve(
                                RentalTermType.DATE_RANGE,
                                TODAY,
                                TODAY.plusDays(29),
                                null
                        ).durationDays()
                ),
                () -> Assertions.assertThrows(
                        RentalMaximumStayExceededException.class,
                        () -> policy.resolve(
                                RentalTermType.DATE_RANGE,
                                TODAY,
                                TODAY.plusDays(30),
                                null
                        )
                )
        );
    }

    @Test
    void bookingHorizon_usesCalendarMonthsAndDoesNotRestrictCheckout() {
        LocalDate lastCheckIn = TODAY.plusMonths(6);

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        28,
                        policy.resolve(
                                RentalTermType.MONTHLY,
                                lastCheckIn,
                                null,
                                1
                        ).durationDays()
                ),
                () -> Assertions.assertThrows(
                        RentalBookingHorizonExceededException.class,
                        () -> policy.resolve(
                                RentalTermType.MONTHLY,
                                lastCheckIn.plusDays(1),
                                null,
                                1
                        )
                )
        );
    }

    @Test
    void monthly_usesCalendarMonthsIncludingEndOfMonthSemantics() {
        var properties = new RentalProperties(
                7,
                30,
                new BigDecimal("0.10"),
                365,
                6,
                3,
                ZoneId.of("Europe/Istanbul")
        );
        RentalStayPolicy januaryPolicy = policyAt(properties, LocalDate.of(2026, 1, 1));

        ResolvedRentalTerm term = januaryPolicy.resolve(
                RentalTermType.MONTHLY,
                LocalDate.of(2026, 1, 31),
                null,
                1
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(LocalDate.of(2026, 2, 28), term.checkOutDate()),
                () -> Assertions.assertEquals(28, term.durationDays()),
                () -> Assertions.assertEquals(1, term.rentalMonths())
        );
    }

    @Test
    void mismatchedTermFieldsAndInvalidDates_rejected() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        InvalidRentalDateRangeException.class,
                        () -> policy.resolve(
                                RentalTermType.DATE_RANGE,
                                TODAY.minusDays(1),
                                TODAY.plusDays(7),
                                null
                        )
                ),
                () -> Assertions.assertThrows(
                        InvalidRentalDateRangeException.class,
                        () -> policy.resolve(
                                RentalTermType.DATE_RANGE,
                                TODAY,
                                TODAY,
                                null
                        )
                ),
                () -> Assertions.assertThrows(
                        InvalidRentalBookingException.class,
                        () -> policy.resolve(
                                RentalTermType.DATE_RANGE,
                                TODAY,
                                TODAY.plusDays(7),
                                1
                        )
                ),
                () -> Assertions.assertThrows(
                        InvalidRentalBookingException.class,
                        () -> policy.resolve(
                                RentalTermType.MONTHLY,
                                TODAY,
                                TODAY.plusMonths(1),
                                1
                        )
                )
        );
    }

    private static RentalStayPolicy policyAt(
            RentalProperties properties,
            LocalDate today
    ) {
        return new RentalStayPolicy(
                properties,
                Clock.fixed(today.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        );
    }
}
