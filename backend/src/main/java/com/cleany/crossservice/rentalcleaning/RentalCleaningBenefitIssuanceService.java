package com.cleany.crossservice.rentalcleaning;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.cleany.rental.RentalBookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalCleaningBenefitIssuanceService {

    private static final Logger log = LoggerFactory.getLogger(
            RentalCleaningBenefitIssuanceService.class
    );

    private final RentalBookingRepository bookingRepository;
    private final RentalCleaningBenefitIssuer issuer;
    private final RentalCleaningBenefitProperties properties;

    public RentalCleaningBenefitIssuanceResult issueEligible(LocalDate today, int batchSize) {
        List<Long> bookingIds = bookingRepository.findRentalCleaningBenefitCandidates(
                today,
                today.plusDays(properties.checkoutWindowDays()),
                batchSize
        );
        int issued = 0;
        int alreadyExists = 0;
        int ineligible = 0;
        int failed = 0;
        for (long bookingId : bookingIds) {
            try {
                switch (issuer.issue(bookingId, today)) {
                    case ISSUED -> issued++;
                    case ALREADY_EXISTS -> alreadyExists++;
                    case INELIGIBLE -> ineligible++;
                }
            } catch (DataIntegrityViolationException exception) {
                alreadyExists++;
                log.warn(
                        "Duplicate rental cleaning benefit issuance prevented: bookingId={}",
                        bookingId
                );
            } catch (RuntimeException exception) {
                failed++;
                log.error(
                        "Rental cleaning benefit issuance failed: bookingId={}",
                        bookingId,
                        exception
                );
            }
        }
        return new RentalCleaningBenefitIssuanceResult(
                bookingIds.size(),
                issued,
                alreadyExists,
                ineligible,
                failed
        );
    }
}
