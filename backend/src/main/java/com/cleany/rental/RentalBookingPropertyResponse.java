package com.cleany.rental;

public record RentalBookingPropertyResponse(
        long id,
        String slug,
        String titleRu,
        String titleEn,
        String area
) {

    static RentalBookingPropertyResponse from(RentalProperty property) {
        return new RentalBookingPropertyResponse(
                property.getId(),
                property.getSlug(),
                property.getTitleRu(),
                property.getTitleEn(),
                property.getArea()
        );
    }
}
