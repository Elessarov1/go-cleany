package com.cleany.crossservice.rentalcleaning;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cleany.observability.SchedulerJobTelemetry;
import com.cleany.observability.SchedulerRunSummary;
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

    private static final String JOB_NAME = "rental-cleaning-benefit";

    private final RentalCleaningBenefitProperties properties;
    private final RentalCleaningBenefitIssuanceService issuanceService;
    private final RentalStayPolicy stayPolicy;
    private final SchedulerJobTelemetry telemetry;
    private final Clock clock;

    @Scheduled(
            cron = "${rental-cleaning-benefit.issuance-cron}",
            zone = "${rental.zone-id}"
    )
    public void run() {
        Instant startedAt = clock.instant();
        long startedNanos = System.nanoTime();
        try {
            RentalCleaningBenefitIssuanceResult result = issuanceService.issueEligible(
                    stayPolicy.today(),
                    properties.issuanceBatchSize()
            );
            long skipped = (long) result.alreadyExists() + result.ineligible();
            telemetry.completed(
                    JOB_NAME,
                    startedAt,
                    elapsed(startedNanos),
                    new SchedulerRunSummary(
                            result.candidates(),
                            result.issued(),
                            skipped,
                            result.failed()
                    )
            );
        } catch (RuntimeException exception) {
            telemetry.failed(
                    JOB_NAME,
                    startedAt,
                    elapsed(startedNanos),
                    SchedulerRunSummary.failedRun(),
                    exception
            );
            throw exception;
        }
    }

    private static Duration elapsed(long startedNanos) {
        return Duration.ofNanos(System.nanoTime() - startedNanos);
    }
}
