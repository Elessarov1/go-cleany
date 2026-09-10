package com.cleany.customer;

import java.time.Instant;
import java.util.UUID;

public record ReauthenticationChallengeResponse(UUID id, ExternalIdentityProvider provider,
                                                String nonce, Instant expiresAt) {
}
