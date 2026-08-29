package com.cleany.rental;

import static com.cleany.common.text.TextValues.normalizeOptional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public record RentalPropertyDetails(
        String titleRu,
        String titleEn,
        String descriptionEn,
        String area,
        String address,
        Integer bedrooms,
        Integer beds,
        Integer bathrooms,
        Integer maxGuests,
        BigDecimal areaSqm,
        Integer floor,
        BigDecimal baseDailyPrice,
        String currency,
        Set<RentalAmenity> amenities
) {

    private static final Pattern CURRENCY_PATTERN = Pattern.compile("^[A-Z]{3}$");

    public RentalPropertyDetails {
        titleRu = normalizeOptional(titleRu);
        titleEn = normalizeOptional(titleEn);
        descriptionEn = normalizeOptional(descriptionEn);
        area = normalizeOptional(area);
        address = normalizeOptional(address);
        currency = normalizeCurrency(currency);
        amenities = amenities == null ? Collections.emptySet() : Set.copyOf(amenities);

        requireMinimum(bedrooms, 0, "bedrooms");
        requireMinimum(beds, 1, "beds");
        requireMinimum(bathrooms, 1, "bathrooms");
        requireMinimum(maxGuests, 1, "maxGuests");
        if (areaSqm != null && areaSqm.signum() <= 0) {
            throw new IllegalArgumentException("areaSqm must be positive");
        }
        if (baseDailyPrice != null && baseDailyPrice.signum() <= 0) {
            throw new IllegalArgumentException("baseDailyPrice must be positive");
        }
    }

    private static String normalizeCurrency(String value) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            return null;
        }
        normalized = normalized.toUpperCase(Locale.ROOT);
        if (!CURRENCY_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("currency must be a three-letter ISO code");
        }
        return normalized;
    }

    private static void requireMinimum(Integer value, int minimum, String name) {
        if (value != null && value < minimum) {
            throw new IllegalArgumentException(name + " must be at least " + minimum);
        }
    }
}
