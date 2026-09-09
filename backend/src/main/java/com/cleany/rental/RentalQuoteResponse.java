package com.cleany.rental;

public record RentalQuoteResponse(
        RentalBookingPropertyResponse property,
        RentalSearchCriteriaResponse criteria,
        RentalPriceResponse price
) {

    static RentalQuoteResponse from(
            RentalProperty property,
            ResolvedRentalTerm term,
            int guests,
            RentalPriceQuote price
    ) {
        return new RentalQuoteResponse(
                RentalBookingPropertyResponse.from(property),
                RentalSearchCriteriaResponse.from(term, guests),
                RentalPriceResponse.from(price)
        );
    }
}
