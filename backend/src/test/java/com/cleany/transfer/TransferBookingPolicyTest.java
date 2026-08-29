package com.cleany.transfer;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TransferBookingPolicyTest {

    private static final ZoneId ISTANBUL = ZoneId.of("Europe/Istanbul");
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-08-29T21:30:00Z"),
            ISTANBUL
    );
    private final TransferBookingPolicy policy = new TransferBookingPolicy(
            new TransferProperties(1, 6, 30, ISTANBUL, TransferAssignmentMode.ADMIN_ASSIGNMENT),
            CLOCK
    );

    @Test
    void todayIsRejectedAndTomorrowIsAcceptedInBusinessTimezone() {
        LocalDate today = LocalDate.of(2026, 8, 30);

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        InvalidTransferBookingException.class,
                        () -> policy.requireBookable(today, LocalTime.of(12, 0))
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> policy.requireBookable(today.plusDays(1), LocalTime.of(12, 30))
                ),
                () -> Assertions.assertThrows(
                        InvalidTransferBookingException.class,
                        () -> policy.requireBookable(today.plusDays(1), LocalTime.of(12, 15))
                )
        );
    }

    @Test
    void configuredBookingHorizonIsEnforced() {
        LocalDate today = LocalDate.of(2026, 8, 30);

        Assertions.assertAll(
                () -> Assertions.assertDoesNotThrow(
                        () -> policy.requireBookable(today.plusMonths(6), LocalTime.MIDNIGHT)
                ),
                () -> Assertions.assertThrows(
                        InvalidTransferBookingException.class,
                        () -> policy.requireBookable(today.plusMonths(6).plusDays(1), LocalTime.MIDNIGHT)
                )
        );
    }
}
