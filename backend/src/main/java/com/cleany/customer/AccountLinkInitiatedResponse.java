package com.cleany.customer;

import java.time.Instant;

public record AccountLinkInitiatedResponse(String deepLink, Instant expiresAt) {
}
