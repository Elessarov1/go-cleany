package com.cleany.media;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MediaValueObjectsTest {

    @Test
    void uploadAndLoadedContent_doNotExposeMutableBinaryArrays() {
        byte[] source = {1, 2, 3};
        var upload = new MediaUpload(source, " Image/PNG ");
        source[0] = 9;
        byte[] uploadContent = upload.content();
        uploadContent[1] = 9;

        var loaded = new MediaContent(
                42L,
                upload.content(),
                upload.contentType(),
                3L,
                "a".repeat(64),
                Instant.parse("2026-08-21T12:00:00Z")
        );
        byte[] loadedContent = loaded.content();
        loadedContent[2] = 9;

        Assertions.assertAll(
                () -> Assertions.assertArrayEquals(new byte[]{1, 2, 3}, upload.content()),
                () -> Assertions.assertEquals("image/png", upload.contentType()),
                () -> Assertions.assertArrayEquals(new byte[]{1, 2, 3}, loaded.content())
        );
    }

    @Test
    void invalidUploadOrInconsistentLoadedContent_rejected() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new MediaUpload(new byte[0], "image/jpeg")
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new MediaUpload(new byte[]{1}, " ")
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new MediaContent(
                                42L,
                                new byte[]{1, 2},
                                "image/jpeg",
                                1L,
                                "a".repeat(64),
                                Instant.parse("2026-08-21T12:00:00Z")
                        )
                )
        );
    }
}
