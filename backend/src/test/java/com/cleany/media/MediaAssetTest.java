package com.cleany.media;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MediaAssetTest {

    private static final Instant CREATED_AT = Instant.parse("2026-08-21T12:00:00Z");
    private static final String SHA256 = "a".repeat(64);

    @Test
    void validContent_metadataNormalizedAndBinaryDefensivelyCopied() {
        byte[] source = {1, 2, 3};
        var asset = new MediaAsset(source, " Image/JPEG ", SHA256.toUpperCase(), CREATED_AT);
        source[0] = 9;
        byte[] returnedContent = asset.getContent();
        returnedContent[1] = 9;

        Assertions.assertAll(
                () -> Assertions.assertArrayEquals(new byte[]{1, 2, 3}, asset.getContent()),
                () -> Assertions.assertEquals("image/jpeg", asset.getContentType()),
                () -> Assertions.assertEquals(3L, asset.getSizeBytes()),
                () -> Assertions.assertEquals(SHA256, asset.getSha256()),
                () -> Assertions.assertEquals(CREATED_AT, asset.getCreatedAt())
        );
    }

    @Test
    void invalidCanonicalMedia_rejectedBeforePersistence() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new MediaAsset(new byte[0], "image/jpeg", SHA256, CREATED_AT)
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new MediaAsset(new byte[]{1}, " ", SHA256, CREATED_AT)
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new MediaAsset(new byte[]{1}, "image/jpeg", "not-a-hash", CREATED_AT)
                )
        );
    }
}
