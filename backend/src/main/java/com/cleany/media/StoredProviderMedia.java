package com.cleany.media;

import java.util.Objects;

public record StoredProviderMedia(
        StoredMedia media,
        MediaProviderReferenceData providerReference
) {

    public StoredProviderMedia {
        media = Objects.requireNonNull(media, "media");
        providerReference = Objects.requireNonNull(providerReference, "providerReference");
        if (media.mediaId() != providerReference.mediaId()) {
            throw new IllegalArgumentException("media and provider reference must belong together");
        }
    }
}
