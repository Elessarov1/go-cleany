package com.cleany.retention;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cleany.observability.SchedulerJobTelemetry;
import com.cleany.observability.SchedulerRunSummary;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(
        prefix = "data-retention",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
public class DataRetentionCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(DataRetentionCleanupJob.class);
    private static final String JOB_NAME = "data-retention";

    private final DataRetentionProperties properties;
    private final DataRetentionCleanupService cleanupService;
    private final Clock clock;
    private final SchedulerJobTelemetry telemetry;

    @Scheduled(cron = "${data-retention.cron}", zone = "${cleaning.zone-id}")
    public void run() {
        long startedAt = System.nanoTime();
        Instant startedInstant = clock.instant();
        Instant cutoff = startedInstant.minus(Duration.ofDays(properties.days()));
        int batchesExecuted = 0;
        long eligibleOrders = 0;
        long deletedIssuePhotos = 0;
        long deletedCompletionPhotos = 0;
        long deletedAuditEvents = 0;
        long deletedMediaAssets = 0;
        try {
            for (int batch = 0; batch < properties.maxBatchesPerRun(); batch++) {
                DataRetentionCleanupResult result = cleanupService.cleanupBatch(
                        cutoff,
                        properties.batchSize()
                );
                batchesExecuted++;
                eligibleOrders += result.eligibleOrderCount();
                deletedIssuePhotos += result.deletedIssuePhotoCount();
                deletedCompletionPhotos += result.deletedCompletionPhotoCount();
                deletedAuditEvents += result.deletedAuditEventCount();
                deletedMediaAssets += result.deletedMediaAssetCount();
                if (!result.hasMoreWork()) {
                    break;
                }
            }
            log.info(
                    "data_retention_result cutoff={} batches={} eligibleOrders={} issuePhotos={} "
                            + "completionPhotos={} auditEvents={} mediaAssets={}",
                    cutoff,
                    batchesExecuted,
                    eligibleOrders,
                    deletedIssuePhotos,
                    deletedCompletionPhotos,
                    deletedAuditEvents,
                    deletedMediaAssets
            );
            telemetry.completed(
                    JOB_NAME,
                    startedInstant,
                    elapsed(startedAt),
                    new SchedulerRunSummary(eligibleOrders, eligibleOrders, 0, 0)
            );
        } catch (RuntimeException exception) {
            telemetry.failed(
                    JOB_NAME,
                    startedInstant,
                    elapsed(startedAt),
                    new SchedulerRunSummary(eligibleOrders, eligibleOrders, 0, 1),
                    exception
            );
        }
    }

    private static Duration elapsed(long startedAt) {
        return Duration.ofNanos(System.nanoTime() - startedAt);
    }
}
