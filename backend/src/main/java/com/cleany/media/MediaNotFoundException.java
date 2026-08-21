package com.cleany.media;

public class MediaNotFoundException extends RuntimeException {

    public MediaNotFoundException(long mediaId) {
        super("Media asset not found: " + mediaId);
    }
}
