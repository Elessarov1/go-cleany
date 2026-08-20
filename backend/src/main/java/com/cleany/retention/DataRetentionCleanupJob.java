package com.cleany.retention;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        prefix = "data-retention",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class DataRetentionCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(DataRetentionCleanupJob.class);

    private final DataRetentionProperties properties;
    private final DataRetentionCleanupService cleanupService;
    private final Clock clock;

    public DataRetentionCleanupJob(
            DataRetentionProperties properties,
            DataRetentionCleanupService cleanupService,
            Clock clock
    ) {
        this.properties = properties;
        this.cleanupService = cleanupService;
        this.clock = clock;
    }

    @Scheduled(cron = "${data-retention.cron}", zone = "${cleaning.zone-id}")
    public void run() {
        long startedAt = System.nanoTime();
        Instant cutoff = clock.instant().minus(Duration.ofDays(properties.days()));
        try {
            DataRetentionCleanupResult result = cleanupService.cleanup(cutoff);
            log.info(
                    "Data retention cleanup completed: cutoff={}, eligibleOrders={}, "
                            + "deletedIssuePhotos={}, deletedCompletionPhotos={}, "
                            + "deletedAuditEvents={}, durationMs={}",
                    result.cutoff(),
                    result.eligibleOrderCount(),
                    result.deletedIssuePhotoCount(),
                    result.deletedCompletionPhotoCount(),
                    result.deletedAuditEventCount(),
                    elapsedMillis(startedAt)
            );
        } catch (RuntimeException exception) {
            log.error(
                    "Data retention cleanup failed: cutoff={}, durationMs={}",
                    cutoff,
                    elapsedMillis(startedAt),
                    exception
            );
        }
    }

    private static long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
