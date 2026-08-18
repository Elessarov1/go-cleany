package com.cleany.configuration;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cleany.order.ApartmentType;
import com.cleany.order.CleaningType;
import com.cleany.order.ServiceArea;

@Service
public class AppConfigurationService {

    private final CleaningProperties properties;

    public AppConfigurationService(CleaningProperties properties) {
        this.properties = properties;
    }

    public AppConfigurationResponse getConfiguration() {
        var apartmentPrices = Arrays.stream(ApartmentType.values())
                .map(type -> new ApartmentPriceResponse(
                        type,
                        properties.prices().regular().priceFor(type),
                        properties.prices().deep().priceFor(type)
                ))
                .toList();

        var duplexSurcharges = new EnumMap<CleaningType, java.math.BigDecimal>(CleaningType.class);
        duplexSurcharges.put(CleaningType.REGULAR, properties.prices().regular().duplexSurcharge());
        duplexSurcharges.put(CleaningType.DEEP, properties.prices().deep().duplexSurcharge());

        return new AppConfigurationResponse(
                List.of(ServiceArea.values()),
                apartmentPrices,
                duplexSurcharges,
                properties.bookingDaysAhead(),
                properties.currency().getCurrencyCode()
        );
    }
}
