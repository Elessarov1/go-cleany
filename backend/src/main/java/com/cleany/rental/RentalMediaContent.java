package com.cleany.rental;

import java.util.Arrays;
import java.util.Objects;

public record RentalMediaContent(String contentType, byte[] content) {

    public RentalMediaContent {
        contentType = Objects.requireNonNull(contentType, "contentType");
        byte[] requiredContent = Objects.requireNonNull(content, "content");
        content = Arrays.copyOf(requiredContent, requiredContent.length);
    }

    @Override
    public byte[] content() {
        return Arrays.copyOf(content, content.length);
    }
}
