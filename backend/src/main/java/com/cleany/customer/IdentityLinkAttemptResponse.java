package com.cleany.customer;

import java.time.Instant;
import java.util.UUID;

public record IdentityLinkAttemptResponse(UUID id, ExternalIdentityProvider provider, String nonce,
                                          String reauthenticationNonce, String telegramDeepLink,
                                          Instant expiresAt) {
}
