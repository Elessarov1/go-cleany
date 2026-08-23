package com.cleany.customer;

import static com.cleany.common.text.TextValues.normalizeOptional;

import java.util.Objects;

public record AuthenticatedCustomerIdentity(
        ExternalIdentityProvider provider,
        String externalSubject,
        String username,
        String displayName,
        String languageCode
) {

    public AuthenticatedCustomerIdentity {
        provider = Objects.requireNonNull(provider, "provider");
        externalSubject = requireText(externalSubject, "externalSubject");
        username = normalizeOptional(username);
        displayName = requireText(displayName, "displayName");
        languageCode = normalizeOptional(languageCode);
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }

}
