package com.cleany.customer;

import static com.cleany.common.text.TextValues.normalizeOptional;

import java.io.Serializable;
import java.util.Objects;

public record AuthenticatedCustomerIdentity(
        ExternalIdentityProvider provider,
        String externalSubject,
        String username,
        String displayName,
        String languageCode,
        String email,
        boolean emailVerified,
        boolean allowsWriteToPm
) implements Serializable {

    public AuthenticatedCustomerIdentity(
            ExternalIdentityProvider provider,
            String externalSubject,
            String username,
            String displayName,
            String languageCode
    ) {
        this(provider, externalSubject, username, displayName, languageCode, null, false, false);
    }

    public AuthenticatedCustomerIdentity(
            ExternalIdentityProvider provider,
            String externalSubject,
            String username,
            String displayName,
            String languageCode,
            String email,
            boolean emailVerified
    ) {
        this(provider, externalSubject, username, displayName, languageCode, email, emailVerified, false);
    }

    public AuthenticatedCustomerIdentity {
        provider = Objects.requireNonNull(provider, "provider");
        externalSubject = requireText(externalSubject, "externalSubject");
        username = normalizeOptional(username);
        displayName = requireText(displayName, "displayName");
        languageCode = normalizeOptional(languageCode);
        email = normalizeEmail(email);
        emailVerified = email != null && emailVerified;
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }

    private static String normalizeEmail(String value) {
        String normalized = normalizeOptional(value);
        return normalized == null ? null : normalized.toLowerCase(java.util.Locale.ROOT);
    }

}
