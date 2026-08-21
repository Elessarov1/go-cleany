package com.cleany.media;

import java.time.Instant;
import java.util.Objects;

public record MediaProviderReferenceData(
        long mediaId,
        MediaProvider provider,
        String externalId,
        String externalUniqueId,
        Instant createdAt
) {

    public MediaProviderReferenceData {
        if (mediaId <= 0) {
            throw new IllegalArgumentException("mediaId must be positive");
        }
        provider = Objects.requireNonNull(provider, "provider");
        externalId = Objects.requireNonNull(externalId, "externalId");
        createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }
}
