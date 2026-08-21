package com.cleany.media;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.cleany.base.BaseIntegrationTest;

class MediaAssetIntegrationTest extends BaseIntegrationTest {

    private static final Instant CREATED_AT = Instant.parse("2026-08-21T12:00:00Z");

    @Autowired
    private MediaAssetRepository assetRepository;

    @Autowired
    private MediaProviderReferenceRepository referenceRepository;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        referenceRepository.deleteAll();
        assetRepository.deleteAll();
    }

    @Test
    void canonicalBinaryAndTelegramReference_persistedThroughLiquibaseSchema() throws Exception {
        byte[] content = "canonical-media".getBytes(StandardCharsets.UTF_8);
        MediaAsset asset = assetRepository.saveAndFlush(new MediaAsset(
                content,
                "image/jpeg",
                sha256(content),
                CREATED_AT
        ));
        MediaProviderReference reference = referenceRepository.saveAndFlush(
                new MediaProviderReference(
                        asset,
                        MediaProvider.TELEGRAM,
                        "telegram-file-id",
                        "telegram-unique-id",
                        CREATED_AT
                )
        );
        referenceRepository.saveAndFlush(new MediaProviderReference(
                asset,
                MediaProvider.WHATSAPP,
                "whatsapp-media-id",
                null,
                CREATED_AT.plusSeconds(1)
        ));

        MediaAsset reloadedAsset = assetRepository.findById(asset.getId()).orElseThrow();
        MediaProviderReference reloadedReference = referenceRepository
                .findByProviderAndExternalUniqueId(MediaProvider.TELEGRAM, "telegram-unique-id")
                .orElseThrow();

        Assertions.assertAll(
                () -> Assertions.assertArrayEquals(content, reloadedAsset.getContent()),
                () -> Assertions.assertEquals("image/jpeg", reloadedAsset.getContentType()),
                () -> Assertions.assertEquals(content.length, reloadedAsset.getSizeBytes()),
                () -> Assertions.assertEquals(sha256(content), reloadedAsset.getSha256()),
                () -> Assertions.assertEquals(asset.getId(), reloadedReference.getMediaAssetId()),
                () -> Assertions.assertEquals(reference.getId(), reloadedReference.getId()),
                () -> Assertions.assertEquals("telegram-file-id", reloadedReference.getExternalId()),
                () -> Assertions.assertEquals(2L, referenceRepository.count()),
                () -> Assertions.assertEquals(
                        asset.getId(),
                        referenceRepository
                                .findByProviderAndExternalId(MediaProvider.WHATSAPP, "whatsapp-media-id")
                                .orElseThrow()
                                .getMediaAssetId()
                )
        );
    }

    @Test
    void providerExternalIdentifier_duplicateRejectedByDatabase() throws Exception {
        byte[] firstContent = "first".getBytes(StandardCharsets.UTF_8);
        byte[] secondContent = "second".getBytes(StandardCharsets.UTF_8);
        MediaAsset first = assetRepository.saveAndFlush(new MediaAsset(
                firstContent,
                "image/jpeg",
                sha256(firstContent),
                CREATED_AT
        ));
        MediaAsset second = assetRepository.saveAndFlush(new MediaAsset(
                secondContent,
                "image/jpeg",
                sha256(secondContent),
                CREATED_AT.plusSeconds(1)
        ));
        referenceRepository.saveAndFlush(new MediaProviderReference(
                first,
                MediaProvider.TELEGRAM,
                "same-file-id",
                "first-unique-id",
                CREATED_AT
        ));

        Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> referenceRepository.saveAndFlush(new MediaProviderReference(
                        second,
                        MediaProvider.TELEGRAM,
                        "same-file-id",
                        "second-unique-id",
                        CREATED_AT.plusSeconds(1)
                ))
        );
    }

    private static String sha256(byte[] content) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
    }
}
