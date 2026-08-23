package com.cleany.rental;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public record RentalPropertyResponse(
        long id,
        String slug,
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
        RentalPropertyStatus status,
        List<RentalAmenity> amenities,
        List<RentalPropertyMediaResponse> media,
        Instant createdAt,
        Instant updatedAt
) {

    static RentalPropertyResponse from(
            RentalProperty property,
            List<RentalPropertyMediaResponse> media
    ) {
        return new RentalPropertyResponse(
                property.getId(),
                property.getSlug(),
                property.getTitleRu(),
                property.getTitleEn(),
                property.getDescriptionEn(),
                property.getArea(),
                property.getAddress(),
                property.getBedrooms(),
                property.getBeds(),
                property.getBathrooms(),
                property.getMaxGuests(),
                property.getAreaSqm(),
                property.getFloor(),
                property.getBaseDailyPrice(),
                property.getCurrency(),
                property.getStatus(),
                property.getAmenities().stream()
                        .sorted(Comparator.comparing(RentalAmenity::name))
                        .toList(),
                List.copyOf(media),
                property.getCreatedAt(),
                property.getUpdatedAt()
        );
    }
}
