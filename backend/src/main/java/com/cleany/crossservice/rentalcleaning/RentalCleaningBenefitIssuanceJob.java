package com.cleany.crossservice.rentalcleaning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cleany.rental.RentalStayPolicy;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(
        prefix = "rental-cleaning-benefit",
        name = "issuance-enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
public class RentalCleaningBenefitIssuanceJob {

    private static final Logger log = LoggerFactory.getLogger(
            RentalCleaningBenefitIssuanceJob.class
    );

    private final RentalCleaningBenefitProperties properties;
    private final RentalCleaningBenefitIssuanceService issuanceService;
    private final RentalStayPolicy stayPolicy;

    @Scheduled(
            cron = "${rental-cleaning-benefit.issuance-cron}",
            zone = "${rental.zone-id}"
    )
    public void run() {
        RentalCleaningBenefitIssuanceResult result = issuanceService.issueEligible(
                stayPolicy.today(),
                properties.issuanceBatchSize()
        );
        if (result.candidates() > 0 || result.failed() > 0) {
            log.info(
                    "Rental cleaning benefit issuance completed: candidates={}, issued={}, "
                            + "alreadyExists={}, ineligible={}, failed={}",
                    result.candidates(),
                    result.issued(),
                    result.alreadyExists(),
                    result.ineligible(),
                    result.failed()
            );
        }
    }
}
