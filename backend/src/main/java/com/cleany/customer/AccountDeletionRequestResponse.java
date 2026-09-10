package com.cleany.customer;

import java.time.Instant;
import java.util.UUID;

public record AccountDeletionRequestResponse(UUID id, ExternalIdentityProvider provider,
                                             String nonce, Instant expiresAt) {
}
