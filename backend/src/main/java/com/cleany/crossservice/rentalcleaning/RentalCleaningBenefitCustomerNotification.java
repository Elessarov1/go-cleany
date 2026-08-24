package com.cleany.crossservice.rentalcleaning;

import java.time.LocalDate;

import com.cleany.notification.CustomerNotification;

public record RentalCleaningBenefitCustomerNotification(
        long rentalBookingId,
        String code,
        LocalDate earliestCleaningDate,
        LocalDate checkOutDate
) implements CustomerNotification {
}
