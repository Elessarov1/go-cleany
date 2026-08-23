package com.cleany.common.text;

import java.util.Objects;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TextValues {

    public static String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public static String requireNonBlank(
            String value,
            int maxLength,
            Function<String, ? extends RuntimeException> exceptionFactory
    ) {
        Objects.requireNonNull(exceptionFactory, "exceptionFactory");
        if (maxLength <= 0) {
            throw new IllegalArgumentException("maxLength must be positive");
        }
        if (value == null || value.isBlank()) {
            throw exceptionFactory.apply("must not be blank");
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw exceptionFactory.apply("is too long");
        }
        return normalized;
    }
}
