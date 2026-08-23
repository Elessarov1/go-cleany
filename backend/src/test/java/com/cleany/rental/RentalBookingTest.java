package com.cleany.rental;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RentalBookingTest {

    private static final Instant NOW = Instant.parse("2026-08-23T09:00:00Z");

    @Test
    void customerCancellation_onlyBeforeCheckInAndOnlyFromConfirmed() {
        RentalBooking booking = booking(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 8));

        booking.cancelByCustomer(LocalDate.of(2026, 8, 31), NOW.plusSeconds(1));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        RentalBookingStatus.CANCELLED_BY_CUSTOMER,
                        booking.getStatus()
                ),
                () -> Assertions.assertNotNull(booking.getCancelledAt()),
                () -> Assertions.assertThrows(
                        RentalBookingCannotBeCancelledException.class,
                        () -> booking.cancelByCustomer(LocalDate.of(2026, 8, 31), NOW.plusSeconds(2))
                )
        );
    }

    @Test
    void customerCancellation_onCheckInRejected() {
        RentalBooking booking = booking(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 8));

        Assertions.assertThrows(
                RentalBookingCannotBeCancelledException.class,
                () -> booking.cancelByCustomer(LocalDate.of(2026, 9, 1), NOW.plusSeconds(1))
        );
    }

    @Test
    void completion_beforeCheckoutRejected() {
        RentalBooking booking = booking(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 8));

        Assertions.assertThrows(
                RentalBookingCannotBeCompletedException.class,
                () -> booking.complete(LocalDate.of(2026, 9, 7), NOW.plusSeconds(1))
        );
    }

    @Test
    void completion_onCheckoutAccepted() {
        LocalDate checkout = LocalDate.of(2026, 9, 8);
        RentalBooking booking = booking(LocalDate.of(2026, 9, 1), checkout);

        booking.complete(checkout, NOW.plusSeconds(1));

        Assertions.assertAll(
                () -> Assertions.assertEquals(RentalBookingStatus.COMPLETED, booking.getStatus()),
                () -> Assertions.assertNotNull(booking.getCompletedAt())
        );
    }

    private static RentalBooking booking(LocalDate checkIn, LocalDate checkOut) {
        RentalProperty property = new RentalProperty(NOW);
        property.updateDetails(
                RentalPropertyTest.completeDetails(new BigDecimal("100.00")),
                NOW
        );
        var quote = new RentalPriceQuote(
                RentalTermType.DATE_RANGE,
                null,
                Math.toIntExact(java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut)),
                new BigDecimal("100.00"),
                null,
                new BigDecimal("700.00"),
                false,
                BigDecimal.ZERO,
                new BigDecimal("0.00"),
                new BigDecimal("700.00"),
                "TRY"
        );
        return new RentalBooking(
                1L,
                2L,
                property,
                new ResolvedRentalTerm(
                        RentalTermType.DATE_RANGE,
                        checkIn,
                        checkOut,
                        Math.toIntExact(java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut)),
                        null
                ),
                "Alex",
                "+905551234567",
                2,
                null,
                quote,
                NOW
        );
    }
}
