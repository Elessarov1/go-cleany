package com.cleany.media;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cleany.base.BaseIntegrationTest;

class PostgresMediaStorageIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MediaStorage mediaStorage;

    @Autowired
    private MediaAssetRepository assetRepository;

    @Autowired
    private MediaProviderReferenceRepository referenceRepository;

    @BeforeEach
    void cleanDatabase() {
        referenceRepository.deleteAll();
        assetRepository.deleteAll();
    }

    @Test
    void storeAndGet_canonicalBytesAndMetadataRoundTrip() throws NoSuchAlgorithmException {
        byte[] source = "stored-in-postgres".getBytes(StandardCharsets.UTF_8);
        byte[] expected = source.clone();
        StoredMedia stored = mediaStorage.store(new MediaUpload(source, " Image/JPEG "));
        source[0] = 0;

        MediaContent loaded = mediaStorage.get(stored.mediaId());
        byte[] callerCopy = loaded.content();
        callerCopy[0] = 0;
        MediaContent loadedAgain = mediaStorage.get(stored.mediaId());

        Assertions.assertAll(
                () -> Assertions.assertTrue(stored.mediaId() > 0),
                () -> Assertions.assertEquals("image/jpeg", stored.contentType()),
                () -> Assertions.assertEquals(expected.length, stored.sizeBytes()),
                () -> Assertions.assertEquals(sha256(expected), stored.sha256()),
                () -> Assertions.assertNotNull(stored.createdAt()),
                () -> Assertions.assertArrayEquals(expected, loaded.content()),
                () -> Assertions.assertArrayEquals(expected, loadedAgain.content()),
                () -> Assertions.assertEquals(stored.sha256(), loaded.sha256())
        );
    }

    @Test
    void delete_removesProviderReferencesThenCanonicalAsset() {
        StoredMedia stored = mediaStorage.store(new MediaUpload(new byte[]{1, 2, 3}, "image/png"));
        MediaAsset asset = assetRepository.findById(stored.mediaId()).orElseThrow();
        referenceRepository.saveAndFlush(new MediaProviderReference(
                asset,
                MediaProvider.TELEGRAM,
                "telegram-file-id",
                "telegram-unique-id",
                stored.createdAt()
        ));

        mediaStorage.delete(stored.mediaId());

        Assertions.assertAll(
                () -> Assertions.assertFalse(assetRepository.existsById(stored.mediaId())),
                () -> Assertions.assertEquals(0L, referenceRepository.count()),
                () -> Assertions.assertThrows(
                        MediaNotFoundException.class,
                        () -> mediaStorage.get(stored.mediaId())
                )
        );
    }

    @Test
    void unknownMedia_getAndDeleteRejected() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        MediaNotFoundException.class,
                        () -> mediaStorage.get(999999L)
                ),
                () -> Assertions.assertThrows(
                        MediaNotFoundException.class,
                        () -> mediaStorage.delete(999999L)
                )
        );
    }

    private static String sha256(byte[] content) throws NoSuchAlgorithmException {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
    }
}
