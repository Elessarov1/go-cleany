package com.cleany.configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.cleany.order.CleaningType;
import com.cleany.order.ServiceArea;

public record AppConfigurationResponse(
        List<ServiceArea> areas,
        List<ApartmentPriceResponse> apartmentTypes,
        Map<CleaningType, BigDecimal> duplexSurcharges,
        int bookingDaysAhead,
        String currency
) {
}

