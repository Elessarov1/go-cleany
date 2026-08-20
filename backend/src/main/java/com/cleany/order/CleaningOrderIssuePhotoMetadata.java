package com.cleany.order;

import java.time.Instant;

public record CleaningOrderIssuePhotoMetadata(
        long id,
        String contentType,
        long sizeBytes,
        String sha256,
        Instant createdAt
) {
}
