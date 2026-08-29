package com.cleany.transfer;

import java.time.Instant;

public record TransferDriverLinkResponse(String url, Instant expiresAt) {
}
