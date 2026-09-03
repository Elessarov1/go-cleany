package com.cleany.rental;

import java.util.Objects;

/**
 * Request-scoped response buffer. The platform storage boundary already made the defensive copy;
 * this adapter and its HTTP caller consume that buffer without copying it again.
 */

public record RentalMediaContent(String contentType, byte[] content) {

    public RentalMediaContent {
        contentType = Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(content, "content");
    }
}
