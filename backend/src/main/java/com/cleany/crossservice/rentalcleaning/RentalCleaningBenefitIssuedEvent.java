package com.cleany.crossservice.rentalcleaning;

public record RentalCleaningBenefitIssuedEvent(
        long benefitId,
        long rentalBookingId,
        long customerId,
        long communicationIdentityId
) {
}
