package com.cleany.crossservice.rentalcleaning;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.rental.RentalBooking;
import com.cleany.rental.RentalBookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalCleaningBenefitNotificationQueryService {

    private final RentalCleaningBenefitRepository benefitRepository;
    private final RentalBookingRepository bookingRepository;
    private final RentalCleaningBenefitProperties properties;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public RentalCleaningBenefitCustomerNotification issued(long benefitId) {
        RentalCleaningBenefit benefit = benefitRepository.findById(benefitId)
                .orElseThrow(() -> new IllegalStateException(
                        "Rental cleaning benefit not found: " + benefitId
                ));
        RentalBooking booking = bookingRepository.findById(benefit.getRentalBookingId())
                .orElseThrow(() -> new IllegalStateException(
                        "Source rental booking not found: " + benefit.getRentalBookingId()
                ));
        return new RentalCleaningBenefitCustomerNotification(
                benefit.getId(),
                booking.getId(),
                benefit.getCode(),
                booking.getCheckOutDate().minusDays(properties.checkoutWindowDays()),
                booking.getCheckOutDate()
        );
    }
}
