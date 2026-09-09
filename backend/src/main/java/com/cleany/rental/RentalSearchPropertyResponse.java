package com.cleany.rental;

import java.math.BigDecimal;

public record RentalSearchPropertyResponse(
        long id,
        String slug,
        String titleRu,
        String titleEn,
        String descriptionEn,
        String area,
        int bedrooms,
        int maxGuests,
        BigDecimal areaSqm,
        BigDecimal baseDailyPrice,
        String currency,
        String coverUrl,
        RentalPriceResponse price
) {

    static RentalSearchPropertyResponse from(
            RentalSearchPropertyRow property,
            String coverUrl,
            RentalPriceQuote quote
    ) {
        return new RentalSearchPropertyResponse(
                property.id(),
                property.slug(),
                property.titleRu(),
                property.titleEn(),
                property.descriptionEn(),
                property.area(),
                property.bedrooms(),
                property.maxGuests(),
                property.areaSqm(),
                property.baseDailyPrice(),
                property.currency(),
                coverUrl,
                quote == null ? null : RentalPriceResponse.from(quote)
        );
    }
}
