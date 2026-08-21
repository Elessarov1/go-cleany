package com.cleany.media;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.HexFormat;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostgresMediaStorage implements MediaStorage {

    private final MediaAssetRepository assetRepository;
    private final MediaProviderReferenceRepository referenceRepository;
    private final Clock clock;

    public PostgresMediaStorage(
            MediaAssetRepository assetRepository,
            MediaProviderReferenceRepository referenceRepository,
            Clock clock
    ) {
        this.assetRepository = assetRepository;
        this.referenceRepository = referenceRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public StoredMedia store(MediaUpload upload) {
        MediaUpload requiredUpload = Objects.requireNonNull(upload, "upload");
        byte[] content = requiredUpload.content();
        MediaAsset asset = assetRepository.saveAndFlush(new MediaAsset(
                content,
                requiredUpload.contentType(),
                sha256(content),
                clock.instant()
        ));
        return metadata(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public MediaContent get(long mediaId) {
        MediaAsset asset = find(mediaId);
        return new MediaContent(
                asset.getId(),
                asset.getContent(),
                asset.getContentType(),
                asset.getSizeBytes(),
                asset.getSha256(),
                asset.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public void delete(long mediaId) {
        MediaAsset asset = find(mediaId);
        referenceRepository.deleteAllByMediaAssetId(mediaId);
        assetRepository.delete(asset);
    }

    private MediaAsset find(long mediaId) {
        return assetRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException(mediaId));
    }

    private static StoredMedia metadata(MediaAsset asset) {
        return new StoredMedia(
                asset.getId(),
                asset.getContentType(),
                asset.getSizeBytes(),
                asset.getSha256(),
                asset.getCreatedAt()
        );
    }

    private static String sha256(byte[] content) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
