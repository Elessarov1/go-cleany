package com.cleany.media;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MediaProviderReferenceTest {

    private static final Instant CREATED_AT = Instant.parse("2026-08-21T12:00:00Z");

    @Test
    void providerMetadata_normalizedWithoutChangingCanonicalAsset() {
        MediaAsset asset = asset();
        var reference = new MediaProviderReference(
                asset,
                MediaProvider.TELEGRAM,
                " telegram-file-id ",
                " telegram-unique-id ",
                CREATED_AT
        );

        Assertions.assertAll(
                () -> Assertions.assertSame(asset, reference.getMediaAsset()),
                () -> Assertions.assertEquals(MediaProvider.TELEGRAM, reference.getProvider()),
                () -> Assertions.assertEquals("telegram-file-id", reference.getExternalId()),
                () -> Assertions.assertEquals("telegram-unique-id", reference.getExternalUniqueId()),
                () -> Assertions.assertEquals(CREATED_AT, reference.getCreatedAt())
        );
    }

    @Test
    void missingProviderIdentifier_rejectedBeforePersistence() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new MediaProviderReference(
                                asset(),
                                MediaProvider.TELEGRAM,
                                " ",
                                "unique-id",
                                CREATED_AT
                        )
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new MediaProviderReference(
                                asset(),
                                MediaProvider.TELEGRAM,
                                "file-id",
                                " ",
                                CREATED_AT
                        )
                )
        );
    }

    private static MediaAsset asset() {
        return new MediaAsset(new byte[]{1}, "image/jpeg", "a".repeat(64), CREATED_AT);
    }
}
