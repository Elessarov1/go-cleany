package com.cleany.media;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

public record MediaContent(
        long mediaId,
        byte[] content,
        String contentType,
        long sizeBytes,
        String sha256,
        Instant createdAt
) {

    public MediaContent {
        if (mediaId <= 0) {
            throw new IllegalArgumentException("mediaId must be positive");
        }
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("content must not be empty");
        }
        content = Arrays.copyOf(content, content.length);
        contentType = Objects.requireNonNull(contentType, "contentType");
        if (sizeBytes != content.length) {
            throw new IllegalArgumentException("sizeBytes must match content length");
        }
        sha256 = Objects.requireNonNull(sha256, "sha256");
        createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    @Override
    public byte[] content() {
        return Arrays.copyOf(content, content.length);
    }
}
