package com.cleany.rental;

import java.math.BigDecimal;
import java.time.Instant;
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
        RentalPriceQuote quote = priceService.calculate(property, 29);

        Assertions.assertAll(
                () -> Assertions.assertFalse(quote.longTermDiscountApplied()),
                () -> Assertions.assertEquals("2900.00", quote.baseAmount().toPlainString()),
                () -> Assertions.assertEquals("0.00", quote.discountAmount().toPlainString()),
                () -> Assertions.assertEquals("2900.00", quote.totalPrice().toPlainString())
        );
    }

    @Test
    void thirtyDays_longTermDiscountApplied() {
        RentalPriceQuote quote = priceService.calculate(property, 30);

        Assertions.assertAll(
                () -> Assertions.assertTrue(quote.longTermDiscountApplied()),
                () -> Assertions.assertEquals(new BigDecimal("0.10"), quote.discountRate()),
                () -> Assertions.assertEquals("300.00", quote.discountAmount().toPlainString()),
                () -> Assertions.assertEquals("2700.00", quote.totalPrice().toPlainString())
        );
    }
}
