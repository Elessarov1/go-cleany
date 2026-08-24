package com.cleany.crossservice.rentalcleaning;

public record RentalCleaningBenefitIssuanceResult(
        int candidates,
        int issued,
        int alreadyExists,
        int ineligible,
        int failed
) {
}
