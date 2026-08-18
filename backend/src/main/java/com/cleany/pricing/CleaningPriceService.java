package com.cleany.pricing;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.cleany.configuration.CleaningProperties;
import com.cleany.order.ApartmentType;
import com.cleany.order.CleaningType;

@Service
public class CleaningPriceService {

    private final CleaningProperties properties;

    public CleaningPriceService(CleaningProperties properties) {
        this.properties = properties;
    }

    public BigDecimal calculate(
            ApartmentType apartmentType,
            CleaningType cleaningType,
            boolean duplex
    ) {
        var priceGroup = switch (cleaningType) {
            case REGULAR -> properties.prices().regular();
            case DEEP -> properties.prices().deep();
        };

        var price = priceGroup.priceFor(apartmentType);
        return duplex ? price.add(priceGroup.duplexSurcharge()) : price;
    }
}

