package com.cleany.configuration;

import java.math.BigDecimal;

import com.cleany.order.ApartmentType;

public record ApartmentPriceResponse(
        ApartmentType type,
        BigDecimal regularPrice,
        BigDecimal deepPrice
) {
}

