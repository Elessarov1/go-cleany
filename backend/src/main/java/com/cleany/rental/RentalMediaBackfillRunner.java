package com.cleany.rental;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
class RentalMediaBackfillRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RentalMediaBackfillRunner.class);

    private final RentalMediaProperties properties;
    private final RentalMediaBackfillBatchProcessor batchProcessor;
    private final RentalMediaBackfillHealthIndicator healthIndicator;

    @Override
    public void run(ApplicationArguments arguments) {
        if (!properties.backfillEnabled()) {
            log.info("Rental media responsive-variant backfill is disabled");
            return;
        }

        healthIndicator.markRunning();
        long totalProcessed = 0;
        log.info(
                "Rental media responsive-variant backfill started: batchSize={}",
                properties.backfillBatchSize()
        );
        try {
            while (true) {
                int processed = batchProcessor.processNextBatch(properties.backfillBatchSize());
                totalProcessed += processed;
                if (processed == 0) {
                    break;
                }
            }
            healthIndicator.markCompleted(totalProcessed);
            log.info(
                    "Rental media responsive-variant backfill completed: processed={}",
                    totalProcessed
            );
        } catch (RuntimeException exception) {
            healthIndicator.markFailed(exception);
            log.error(
                    "Rental media responsive-variant backfill failed after processing {} media rows",
                    totalProcessed,
                    exception
            );
            throw exception;
        }
    }
}
