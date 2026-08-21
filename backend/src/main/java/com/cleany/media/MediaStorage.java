package com.cleany.media;

public interface MediaStorage {

    StoredMedia store(MediaUpload upload);

    MediaContent get(long mediaId);

    void delete(long mediaId);
}
