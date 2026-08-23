package com.cleany.rental;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RentalPriceServiceTest {

    private RentalProperty property;
    private RentalPriceService priceService;

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
        priceService = new RentalPriceService(properties);
        property = new RentalProperty(Instant.parse("2026-08-23T09:00:00Z"));
        property.updateDetails(
                RentalPropertyTest.completeDetails(new BigDecimal("100.00")),
                Instant.parse("2026-08-23T09:01:00Z")
        );
    }

    @Test
    void twentyNineDays_noLongTermDiscount() {
        RentalPriceQuote quote = priceService.calculate(
                property,
                dateRange(LocalDate.of(2026, 9, 1), 29)
        );

        Assertions.assertAll(
                () -> Assertions.assertFalse(quote.longTermDiscountApplied()),
                () -> Assertions.assertEquals("2900.00", quote.baseAmount().toPlainString()),
                () -> Assertions.assertEquals("0.00", quote.discountAmount().toPlainString()),
                () -> Assertions.assertEquals("2900.00", quote.totalPrice().toPlainString())
        );
    }

    @Test
    void monthlyPrice_usesThirtyDayBaseAndConfiguredDiscount() {
        RentalPriceQuote quote = priceService.calculate(
                property,
                monthly(LocalDate.of(2026, 2, 1), 3)
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(RentalTermType.MONTHLY, quote.termType()),
                () -> Assertions.assertEquals(3, quote.rentalMonths()),
                () -> Assertions.assertTrue(quote.longTermDiscountApplied()),
                () -> Assertions.assertEquals(new BigDecimal("0.10"), quote.discountRate()),
                () -> Assertions.assertEquals("2700.00", quote.monthlyPrice().toPlainString()),
                () -> Assertions.assertEquals("900.00", quote.discountAmount().toPlainString()),
                () -> Assertions.assertEquals("8100.00", quote.totalPrice().toPlainString())
        );
    }

    @Test
    void monthlyPrice_doesNotDependOnCalendarDayCount() {
        RentalPriceQuote february = priceService.calculate(
                property,
                monthly(LocalDate.of(2026, 2, 1), 1)
        );
        RentalPriceQuote march = priceService.calculate(
                property,
                monthly(LocalDate.of(2026, 3, 1), 1)
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(28, february.durationDays()),
                () -> Assertions.assertEquals(31, march.durationDays()),
                () -> Assertions.assertEquals(february.monthlyPrice(), march.monthlyPrice()),
                () -> Assertions.assertEquals(february.totalPrice(), march.totalPrice())
        );
    }

    private static ResolvedRentalTerm dateRange(LocalDate start, int days) {
        return new ResolvedRentalTerm(
                RentalTermType.DATE_RANGE,
                start,
                start.plusDays(days),
                days,
                null
        );
    }

    private static ResolvedRentalTerm monthly(LocalDate start, int months) {
        LocalDate end = start.plusMonths(months);
        int days = Math.toIntExact(java.time.temporal.ChronoUnit.DAYS.between(start, end));
        return new ResolvedRentalTerm(
                RentalTermType.MONTHLY,
                start,
                end,
                days,
                months
        );
    }
}
