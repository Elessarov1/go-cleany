package com.cleany.rental;

import java.math.BigDecimal;
import java.time.ZoneId;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RentalPropertiesTest {

    @Test
    void validConfiguration_created() {
        var properties = properties(7, 30, 365);

        Assertions.assertAll(
                () -> Assertions.assertEquals(7, properties.minStayDays()),
                () -> Assertions.assertEquals(30, properties.longTermMinDays()),
                () -> Assertions.assertEquals(365, properties.maxStayDays()),
                () -> Assertions.assertEquals(new BigDecimal("0.10"), properties.longTermDiscountRate())
        );
    }

    @Test
    void longTermThresholdBelowMinimumStay_rejected() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> properties(7, 6, 365)
        );
    }

    @Test
    void maximumStayBelowLongTermThreshold_rejected() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> properties(7, 30, 29)
        );
    }

    private static RentalProperties properties(int minimum, int longTermMinimum, int maximum) {
        return new RentalProperties(
                minimum,
                longTermMinimum,
                new BigDecimal("0.10"),
                maximum,
                6,
                3,
                ZoneId.of("Europe/Istanbul")
        );
    }
}
