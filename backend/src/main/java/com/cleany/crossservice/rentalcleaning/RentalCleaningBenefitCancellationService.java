package com.cleany.crossservice.rentalcleaning;

import java.time.Clock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cleany.rental.RentalBooking;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalCleaningBenefitCancellationService {

    private static final Logger log = LoggerFactory.getLogger(
            RentalCleaningBenefitCancellationService.class
    );

    private final RentalCleaningBenefitRepository benefitRepository;
    private final Clock clock;

    public void revokeAvailableFor(RentalBooking booking) {
        benefitRepository.findByRentalBookingIdForUpdate(booking.getId())
                .filter(benefit -> benefit.revokeIfAvailable(clock.instant()))
                .ifPresent(benefit -> log.info(
                        "Rental cleaning benefit revoked: benefitId={}, bookingId={}",
                        benefit.getId(),
                        booking.getId()
                ));
    }
}
