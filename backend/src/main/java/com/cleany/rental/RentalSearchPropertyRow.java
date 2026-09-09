package com.cleany.rental;

import java.math.BigDecimal;

record RentalSearchPropertyRow(
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
        String currency
) {
}
