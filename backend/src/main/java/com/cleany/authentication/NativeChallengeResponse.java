package com.cleany.authentication;

import java.time.Instant;
import java.util.UUID;

import com.cleany.customer.ExternalIdentityProvider;

public record NativeChallengeResponse(
        UUID challengeId,
        ExternalIdentityProvider provider,
        String nonce,
        Instant expiresAt
) {
}
