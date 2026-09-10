package com.cleany.authentication;

import java.time.Instant;
import java.util.UUID;

public record TelegramLoginAttemptResponse(
        UUID attemptId,
        String botUrl,
        String verifier,
        Instant expiresAt
) {
}
