package com.cleany.pricing;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Currency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.cleany.configuration.CleaningProperties;
import com.cleany.order.ApartmentType;
import com.cleany.order.CleaningType;

class CleaningPriceServiceTest {

    private final CleaningPriceService priceService = new CleaningPriceService(properties());

    @Test
    void regularCleaning_configuredApartment_priceCalculated() {
        BigDecimal price = priceService.calculate(
                ApartmentType.TWO_PLUS_ONE,
                CleaningType.REGULAR,
                false
        );

        Assertions.assertEquals(0, price.compareTo(BigDecimal.valueOf(1100)));
    }

    @Test
    void deepCleaning_duplexApartment_surchargeAddedOnlyOnce() {
        BigDecimal duplexPrice = priceService.calculate(
                ApartmentType.TWO_PLUS_ONE,
                CleaningType.DEEP,
                true
        );
        BigDecimal regularApartmentPrice = priceService.calculate(
                ApartmentType.TWO_PLUS_ONE,
                CleaningType.DEEP,
                false
        );

        Assertions.assertEquals(0, duplexPrice.compareTo(BigDecimal.valueOf(2150)));
        Assertions.assertEquals(0, regularApartmentPrice.compareTo(BigDecimal.valueOf(1700)));
    }

    private static CleaningProperties properties() {
        var regular = new CleaningProperties.PriceGroup(
                amount(800), amount(900), amount(1100), amount(1350), amount(1650), amount(300)
        );
        var deep = new CleaningProperties.PriceGroup(
                amount(1200), amount(1400), amount(1700), amount(2050), amount(2450), amount(450)
        );
        return new CleaningProperties(
                7,
                Currency.getInstance("TRY"),
                ZoneId.of("Europe/Istanbul"),
                new CleaningProperties.Prices(regular, deep)
        );
    }

    private static BigDecimal amount(long value) {
        return BigDecimal.valueOf(value);
    }
}

