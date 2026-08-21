package com.cleany.notification;

import java.util.Objects;

import com.cleany.customer.ExternalIdentityProvider;

public record CommunicationTarget(
        long customerId,
        long externalIdentityId,
        ExternalIdentityProvider provider,
        String externalSubject,
        String languageCode
) {

    public CommunicationTarget {
        if (customerId <= 0) {
            throw new IllegalArgumentException("customerId must be positive");
        }
        if (externalIdentityId <= 0) {
            throw new IllegalArgumentException("externalIdentityId must be positive");
        }
        provider = Objects.requireNonNull(provider, "provider");
        if (externalSubject == null || externalSubject.isBlank()) {
            throw new IllegalArgumentException("externalSubject must not be blank");
        }
        externalSubject = externalSubject.trim();
        languageCode = languageCode == null || languageCode.isBlank()
                ? null
                : languageCode.trim();
    }
}
