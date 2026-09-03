package com.cleany.rental;

import java.util.Objects;

/**
 * Request-scoped response buffer. The platform storage boundary already made the defensive copy;
 * this adapter and its HTTP caller consume that buffer without copying it again.
 */

public record RentalMediaContent(long mediaAssetId, String contentType, byte[] content) {

    public RentalMediaContent {
        if (mediaAssetId <= 0) {
            throw new IllegalArgumentException("mediaAssetId must be positive");
        }
        contentType = Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(content, "content");
    }
}
