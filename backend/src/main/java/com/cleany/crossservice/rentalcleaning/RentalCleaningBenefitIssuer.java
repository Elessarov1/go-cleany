package com.cleany.crossservice.rentalcleaning;

import java.time.Clock;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.rental.RentalBooking;
import com.cleany.rental.RentalBookingRepository;
import com.cleany.rental.RentalBookingStatus;
import com.cleany.catalog.PlatformService;
import com.cleany.catalog.PlatformServiceAccessService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalCleaningBenefitIssuer {

    private static final Logger log = LoggerFactory.getLogger(RentalCleaningBenefitIssuer.class);
    private static final int MAX_CODE_GENERATION_ATTEMPTS = 20;

    private final RentalBookingRepository bookingRepository;
    private final RentalCleaningBenefitRepository benefitRepository;
    private final RentalCleaningBenefitProperties properties;
    private final PlatformServiceAccessService serviceAccessService;
    private final RentalCleaningBenefitCodeGenerator codeGenerator;
    private final Clock clock;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RentalCleaningBenefitIssueOutcome issue(long bookingId, LocalDate today) {
        RentalBooking booking = bookingRepository.findByIdForUpdate(bookingId).orElse(null);
        if (booking == null
                || booking.getStatus() != RentalBookingStatus.CONFIRMED
                || today.isBefore(booking.getCheckOutDate().minusDays(
                        properties.checkoutWindowDays()
                ))
                || today.isAfter(booking.getCheckOutDate())
                || !serviceAccessService.canStartCustomerFlow(
                        PlatformService.CLEANING,
                        booking.getCustomerId()
                )) {
            log.debug(
                    "Rental cleaning benefit issuance skipped: bookingId={}, reason=ineligible",
                    bookingId
            );
            return RentalCleaningBenefitIssueOutcome.INELIGIBLE;
        }
        if (benefitRepository.existsByRentalBookingId(bookingId)) {
            return RentalCleaningBenefitIssueOutcome.ALREADY_EXISTS;
        }

        RentalCleaningBenefit benefit = benefitRepository.saveAndFlush(
                new RentalCleaningBenefit(
                        bookingId,
                        booking.getCustomerId(),
                        nextUniqueCode(),
                        clock.instant()
                )
        );
        eventPublisher.publishEvent(new RentalCleaningBenefitIssuedEvent(
                benefit.getId(),
                bookingId,
                booking.getCustomerId(),
                booking.getCommunicationIdentityId()
        ));
        return RentalCleaningBenefitIssueOutcome.ISSUED;
    }

    private String nextUniqueCode() {
        for (int attempt = 0; attempt < MAX_CODE_GENERATION_ATTEMPTS; attempt++) {
            String candidate = codeGenerator.nextCode();
            if (!benefitRepository.existsByCodeIgnoreCase(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not generate a unique rental cleaning benefit code");
    }
}
