package com.cleany.media;

import java.time.Instant;
import java.util.Objects;

public record StoredMedia(
        long mediaId,
        String contentType,
        long sizeBytes,
        String sha256,
        Instant createdAt
) {

    public StoredMedia {
        if (mediaId <= 0) {
            throw new IllegalArgumentException("mediaId must be positive");
        }
        contentType = Objects.requireNonNull(contentType, "contentType");
        if (sizeBytes <= 0) {
            throw new IllegalArgumentException("sizeBytes must be positive");
        }
        sha256 = Objects.requireNonNull(sha256, "sha256");
        createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }
}
