package com.cleany.notification;

import java.util.Objects;

import com.cleany.media.MediaProvider;

public record ExternalMediaReference(
        MediaProvider provider,
        String externalId
) {

    public ExternalMediaReference {
        provider = Objects.requireNonNull(provider, "provider");
        if (externalId == null || externalId.isBlank()) {
            throw new IllegalArgumentException("externalId must not be blank");
        }
        externalId = externalId.trim();
    }

    public static ExternalMediaReference telegram(String externalId) {
        return new ExternalMediaReference(MediaProvider.TELEGRAM, externalId);
    }
}
