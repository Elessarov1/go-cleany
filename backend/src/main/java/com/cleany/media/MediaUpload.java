package com.cleany.media;

import java.util.Arrays;
import java.util.Locale;

public record MediaUpload(byte[] content, String contentType) {

    private static final int MAX_CONTENT_TYPE_LENGTH = 100;

    public MediaUpload {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("content must not be empty");
        }
        content = Arrays.copyOf(content, content.length);
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("contentType must not be blank");
        }
        contentType = contentType.trim().toLowerCase(Locale.ROOT);
        if (contentType.length() > MAX_CONTENT_TYPE_LENGTH) {
            throw new IllegalArgumentException("contentType is too long");
        }
    }

    @Override
    public byte[] content() {
        return Arrays.copyOf(content, content.length);
    }
}
