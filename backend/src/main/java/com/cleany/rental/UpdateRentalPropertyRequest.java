package com.cleany.rental;

import java.math.BigDecimal;
import java.util.Set;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateRentalPropertyRequest(
        @Size(max = 120)
        @Pattern(regexp = "^[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*$")
        String slug,
        @Size(max = 255) String titleRu,
        @Size(max = 255) String titleEn,
        @Size(max = 5000) String descriptionRu,
        @Size(max = 5000) String descriptionEn,
        @Size(max = 255) String area,
        @Size(max = 1000) String address,
        @Min(0) @Max(100) Integer bedrooms,
        @Min(1) @Max(100) Integer beds,
        @Min(1) @Max(100) Integer bathrooms,
        @Min(1) @Max(100) Integer maxGuests,
        @DecimalMin(value = "0", inclusive = false) BigDecimal areaSqm,
        @Min(-20) @Max(300) Integer floor,
        @DecimalMin(value = "0", inclusive = false) BigDecimal baseDailyPrice,
        @Pattern(regexp = "^[A-Za-z]{3}$") String currency,
        Set<RentalAmenity> amenities
) {

    RentalPropertyDetails toDetails() {
        return new RentalPropertyDetails(
                slug,
                titleRu,
                titleEn,
                descriptionRu,
                descriptionEn,
                area,
                address,
                bedrooms,
                beds,
                bathrooms,
                maxGuests,
                areaSqm,
                floor,
                baseDailyPrice,
                currency,
                amenities
        );
    }
}
