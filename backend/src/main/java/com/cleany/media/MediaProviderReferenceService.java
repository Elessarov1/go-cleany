package com.cleany.media;

import java.time.Clock;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MediaProviderReferenceService {

    private static final int MAX_EXTERNAL_ID_LENGTH = 512;
    private static final int MAX_EXTERNAL_UNIQUE_ID_LENGTH = 255;

    private final MediaStorage mediaStorage;
    private final MediaAssetRepository assetRepository;
    private final MediaProviderReferenceRepository referenceRepository;
    private final Clock clock;

    public MediaProviderReferenceService(
            MediaStorage mediaStorage,
            MediaAssetRepository assetRepository,
            MediaProviderReferenceRepository referenceRepository,
            Clock clock
    ) {
        this.mediaStorage = mediaStorage;
        this.assetRepository = assetRepository;
        this.referenceRepository = referenceRepository;
        this.clock = clock;
    }

    @Transactional
    public StoredProviderMedia resolveOrStore(
            MediaUpload upload,
            MediaProvider provider,
            String externalId,
            String externalUniqueId
    ) {
        MediaUpload requiredUpload = Objects.requireNonNull(upload, "upload");
        String normalizedExternalId = requireValue(
                externalId,
                MAX_EXTERNAL_ID_LENGTH,
                "externalId"
        );
        String normalizedExternalUniqueId = normalizeOptional(
                externalUniqueId,
                MAX_EXTERNAL_UNIQUE_ID_LENGTH,
                "externalUniqueId"
        );
        MediaProvider requiredProvider = Objects.requireNonNull(provider, "provider");
        Optional<MediaProviderReference> existing = normalizedExternalUniqueId == null
                ? Optional.empty()
                : referenceRepository.findByProviderAndExternalUniqueId(
                        requiredProvider,
                        normalizedExternalUniqueId
                );
        if (existing.isEmpty()) {
            existing = referenceRepository.findByProviderAndExternalId(
                    requiredProvider,
                    normalizedExternalId
            );
        }
        if (existing.isPresent()) {
            MediaProviderReference reference = existing.get();
            return new StoredProviderMedia(
                    metadata(reference.getMediaAsset()),
                    data(reference)
            );
        }

        StoredMedia stored = mediaStorage.store(requiredUpload);
        MediaAsset asset = assetRepository.getReferenceById(stored.mediaId());
        MediaProviderReference reference = referenceRepository.save(new MediaProviderReference(
                asset,
                requiredProvider,
                normalizedExternalId,
                normalizedExternalUniqueId,
                clock.instant()
        ));
        return new StoredProviderMedia(stored, data(reference));
    }

    @Transactional(readOnly = true)
    public MediaProviderReferenceData require(long mediaId, MediaProvider provider) {
        MediaProvider requiredProvider = Objects.requireNonNull(provider, "provider");
        return referenceRepository
                .findFirstByMediaAsset_IdAndProviderOrderByCreatedAtDescIdDesc(
                        mediaId,
                        requiredProvider
                )
                .map(MediaProviderReferenceService::data)
                .orElseThrow(() -> new MediaProviderReferenceNotFoundException(
                        mediaId,
                        requiredProvider
                ));
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

    private static MediaProviderReferenceData data(MediaProviderReference reference) {
        return new MediaProviderReferenceData(
                reference.getMediaAssetId(),
                reference.getProvider(),
                reference.getExternalId(),
                reference.getExternalUniqueId(),
                reference.getCreatedAt()
        );
    }

    private static String normalizeOptional(String value, int maxLength, String name) {
        return value == null ? null : requireValue(value, maxLength, name);
    }

    private static String requireValue(String value, int maxLength, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(name + " is too long");
        }
        return normalized;
    }
}
