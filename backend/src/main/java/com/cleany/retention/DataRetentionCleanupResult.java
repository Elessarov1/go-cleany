package com.cleany.retention;

import java.time.Instant;

public record DataRetentionCleanupResult(
        Instant cutoff,
        int eligibleOrderCount,
        int deletedIssuePhotoCount,
        int deletedCompletionPhotoCount,
        int deletedAuditEventCount,
        int deletedMediaAssetCount
) {
}
