package com.cleany.customer;

import java.time.Instant;
import java.util.Objects;

public record AuthenticationContext(
        CustomerContext customer,
        long externalIdentityId,
        ExternalIdentityProvider provider,
        String issuer,
        String subject,
        Instant authenticatedAt
) {
    public AuthenticationContext {
        customer = Objects.requireNonNull(customer, "customer");
        if (externalIdentityId <= 0) {
            throw new IllegalArgumentException("externalIdentityId must be positive");
        }
        provider = Objects.requireNonNull(provider, "provider");
        issuer = Objects.requireNonNull(issuer, "issuer");
        subject = Objects.requireNonNull(subject, "subject");
        authenticatedAt = Objects.requireNonNull(authenticatedAt, "authenticatedAt");
    }
}
