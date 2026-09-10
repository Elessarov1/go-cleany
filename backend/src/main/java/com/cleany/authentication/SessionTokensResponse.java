package com.cleany.authentication;

import java.time.Instant;
import java.util.UUID;

public record SessionTokensResponse(
        UUID sessionId,
        String tokenType,
        String accessToken,
        Instant accessExpiresAt,
        String refreshToken,
        Instant refreshExpiresAt,
        Instant absoluteExpiresAt
) {
}
