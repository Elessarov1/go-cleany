package com.cleany.media;

public class MediaProviderReferenceNotFoundException extends RuntimeException {

    public MediaProviderReferenceNotFoundException(long mediaId, MediaProvider provider) {
        super("Media provider reference not found for asset " + mediaId + " and provider " + provider);
    }
}
