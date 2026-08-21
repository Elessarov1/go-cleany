package com.cleany.notification;

import java.util.Objects;

import com.cleany.customer.ExternalIdentityProvider;

public record ExternalMediaReference(
        ExternalIdentityProvider provider,
        String externalId
) {

    public ExternalMediaReference {
        provider = Objects.requireNonNull(provider, "provider");
        if (externalId == null || externalId.isBlank()) {
            throw new IllegalArgumentException("externalId must not be blank");
        }
        externalId = externalId.trim();
    }

    public static ExternalMediaReference telegram(String externalId) {
        return new ExternalMediaReference(ExternalIdentityProvider.TELEGRAM, externalId);
    }
}
