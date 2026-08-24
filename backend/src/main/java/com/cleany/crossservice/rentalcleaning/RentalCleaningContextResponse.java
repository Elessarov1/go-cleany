package com.cleany.crossservice.rentalcleaning;

import java.time.LocalDate;

public record RentalCleaningContextResponse(
        long rentalBookingId,
        String address,
        String phone,
        LocalDate checkOutDate,
        LocalDate earliestBenefitCleaningDate,
        RentalCleaningBenefitStatus benefitStatus,
        String promoCode
) {
}
