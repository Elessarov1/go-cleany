package com.cleany.telegram.bot;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.cleany.rental.RentalBookingAdminEvent;
import com.cleany.rental.RentalBookingAdminNotification;
import com.cleany.rental.RentalTermType;

class TelegramRentalAdminMessageFactoryTest {

    private final TelegramRentalAdminMessageFactory factory =
            new TelegramRentalAdminMessageFactory();

    @Test
    void dateRangeMessage_containsOperationalBookingDetails() {
        String message = factory.format(
                RentalBookingAdminEvent.Type.CREATED,
                notification(
                        RentalTermType.DATE_RANGE,
                        null,
                        new BigDecimal("2000.00"),
                        null,
                        new BigDecimal("28000.00"),
                        LocalDate.of(2026, 9, 29),
                        14
                )
        );

        Assertions.assertAll(
                () -> Assertions.assertTrue(message.contains("Sea View 1+1")),
                () -> Assertions.assertTrue(message.contains("Alexandr")),
                () -> Assertions.assertTrue(message.contains("+90 555 123 45 67")),
                () -> Assertions.assertTrue(message.contains("15.09.2026 — 29.09.2026")),
                () -> Assertions.assertTrue(message.contains("2 000 TRY / сутки")),
                () -> Assertions.assertTrue(message.contains("28 000 TRY"))
        );
    }

    @Test
    void monthlyCancellationMessage_containsMonthsMonthlyPriceAndExpectedEnd() {
        String message = factory.format(
                RentalBookingAdminEvent.Type.CANCELLED_BY_CUSTOMER,
                notification(
                        RentalTermType.MONTHLY,
                        3,
                        new BigDecimal("2000.00"),
                        new BigDecimal("54000.00"),
                        new BigDecimal("162000.00"),
                        LocalDate.of(2026, 12, 15),
                        91
                )
        );

        Assertions.assertAll(
                () -> Assertions.assertTrue(message.contains("отменено клиентом")),
                () -> Assertions.assertTrue(message.contains("Срок: 3 мес.")),
                () -> Assertions.assertTrue(message.contains("15.12.2026")),
                () -> Assertions.assertTrue(message.contains("54 000 TRY / месяц")),
                () -> Assertions.assertTrue(message.contains("162 000 TRY"))
        );
    }

    private static RentalBookingAdminNotification notification(
            RentalTermType termType,
            Integer months,
            BigDecimal dailyPrice,
            BigDecimal monthlyPrice,
            BigDecimal totalPrice,
            LocalDate checkOut,
            int durationDays
    ) {
        return new RentalBookingAdminNotification(
                42L,
                "Sea View 1+1",
                "Alexandr",
                "+90 555 123 45 67",
                termType,
                LocalDate.of(2026, 9, 15),
                checkOut,
                months,
                durationDays,
                dailyPrice,
                monthlyPrice,
                totalPrice,
                "TRY"
        );
    }
}
