package com.cleany.crossservice.rentalcleaning;

import java.time.LocalDate;

import com.cleany.action.ActionTarget;
import com.cleany.notification.CustomerNotification;
import com.cleany.notification.CustomerNotificationType;

public record RentalCleaningBenefitCustomerNotification(
        long benefitId,
        long rentalBookingId,
        String code,
        LocalDate earliestCleaningDate,
        LocalDate checkOutDate
) implements CustomerNotification {

    @Override
    public CustomerNotificationType type() {
        return CustomerNotificationType.RENTAL_CLEANING_BENEFIT_AVAILABLE;
    }

    @Override
    public ActionTarget action() {
        return new ActionTarget.StartRentalCleaning(rentalBookingId);
    }

    @Override
    public String deduplicationKey() {
        return "rental-cleaning-benefit:" + benefitId + ":available";
    }
}
