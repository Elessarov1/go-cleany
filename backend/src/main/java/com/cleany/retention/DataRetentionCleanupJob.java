package com.cleany.retention;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    private final DataRetentionProperties properties;
    private final DataRetentionCleanupService cleanupService;
    private final Clock clock;

    @Scheduled(cron = "${data-retention.cron}", zone = "${cleaning.zone-id}")
    public void run() {
        long startedAt = System.nanoTime();
        Instant cutoff = clock.instant().minus(Duration.ofDays(properties.days()));
        int batchesExecuted = 0;
        long eligibleOrders = 0;
        long deletedIssuePhotos = 0;
        long deletedCompletionPhotos = 0;
        long deletedAuditEvents = 0;
        long deletedMediaAssets = 0;
        boolean stoppedBecauseNoMoreWork = false;
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
                    stoppedBecauseNoMoreWork = true;
                    break;
                }
            }
            boolean maxBatchesReached = !stoppedBecauseNoMoreWork
                    && batchesExecuted == properties.maxBatchesPerRun();
            log.info(
                    "Data retention cleanup completed: cutoff={}, batchesExecuted={}, eligibleOrders={}, "
                            + "deletedIssuePhotos={}, deletedCompletionPhotos={}, "
                            + "deletedAuditEvents={}, deletedMediaAssets={}, durationMs={}, "
                            + "stoppedBecauseNoMoreWork={}, maxBatchesReached={}",
                    cutoff,
                    batchesExecuted,
                    eligibleOrders,
                    deletedIssuePhotos,
                    deletedCompletionPhotos,
                    deletedAuditEvents,
                    deletedMediaAssets,
                    elapsedMillis(startedAt),
                    stoppedBecauseNoMoreWork,
                    maxBatchesReached
            );
        } catch (RuntimeException exception) {
            log.error(
                    "Data retention cleanup failed: cutoff={}, completedBatches={}, "
                            + "eligibleOrders={}, deletedIssuePhotos={}, deletedCompletionPhotos={}, "
                            + "deletedAuditEvents={}, deletedMediaAssets={}, durationMs={}",
                    cutoff,
                    batchesExecuted,
                    eligibleOrders,
                    deletedIssuePhotos,
                    deletedCompletionPhotos,
                    deletedAuditEvents,
                    deletedMediaAssets,
                    elapsedMillis(startedAt),
                    exception
            );
        }
    }

    private static long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
