package com.cleany.rental;

import java.time.Clock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.media.MediaStorage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalPropertyMediaService {

    private static final int MAX_IMAGE_BYTES = 10 * 1024 * 1024;

    private final RentalPropertyRepository propertyRepository;
    private final RentalPropertyMediaRepository mediaRepository;
    private final MediaStorage mediaStorage;
    private final RentalImageNormalizer imageNormalizer;
    private final Clock clock;

    @Transactional
    public void add(long propertyId, byte[] content, boolean requestedCover) {
        RentalProperty property = requireProperty(propertyId);
        if (content == null || content.length == 0) {
            throw new InvalidRentalPropertyMediaException("Property image must not be empty");
        }
        if (content.length > MAX_IMAGE_BYTES) {
            throw new InvalidRentalPropertyMediaException("Property image exceeds 10 MB");
        }
        RentalImageVariants variants = imageNormalizer.normalize(content);
        List<RentalPropertyMedia> existing = media(propertyId);
        boolean cover = requestedCover || existing.isEmpty();
        if (cover) {
            existing.forEach(item -> item.setCover(false));
            mediaRepository.flush();
        }
        var full = mediaStorage.store(variants.full());
        var card = mediaStorage.store(variants.card());
        var thumbnail = mediaStorage.store(variants.thumbnail());
        mediaRepository.save(new RentalPropertyMedia(
                property,
                full.mediaId(),
                card.mediaId(),
                thumbnail.mediaId(),
                existing.size(),
                cover,
                clock.instant()
        ));
        property.touch(clock.instant());
    }

    @Transactional
    public void remove(long propertyId, long mediaId) {
        RentalProperty property = requireProperty(propertyId);
        RentalPropertyMedia media = requireMedia(propertyId, mediaId);
        List<RentalPropertyMedia> current = media(propertyId);
        if (property.getStatus() == RentalPropertyStatus.PUBLISHED && current.size() == 1) {
            throw new RentalPropertyCannotBePublishedException(
                    "A published rental property must retain at least one image"
            );
        }
        boolean cover = media.isCover();
        List<Long> assetIds = mediaAssetIds(media);
        mediaRepository.delete(media);
        mediaRepository.flush();
        assetIds.forEach(this::deleteAssetIfUnreferenced);
        List<RentalPropertyMedia> remaining = media(propertyId);
        for (int index = 0; index < remaining.size(); index++) {
            RentalPropertyMedia item = remaining.get(index);
            item.reorder(index);
            if (cover && index == 0) {
                item.setCover(true);
            }
        }
        property.touch(clock.instant());
    }

    @Transactional
    public void setCover(long propertyId, long mediaId) {
        RentalProperty property = requireProperty(propertyId);
        RentalPropertyMedia selected = requireMedia(propertyId, mediaId);
        List<RentalPropertyMedia> allMedia = media(propertyId);
        allMedia.stream()
                .filter(RentalPropertyMedia::isCover)
                .forEach(item -> item.setCover(false));
        mediaRepository.flush();
        selected.setCover(true);
        property.touch(clock.instant());
    }

    @Transactional
    public void reorder(long propertyId, List<Long> orderedMediaIds) {
        RentalProperty property = requireProperty(propertyId);
        List<RentalPropertyMedia> current = media(propertyId);
        if (orderedMediaIds == null
                || current.size() != orderedMediaIds.size()
                || new HashSet<>(orderedMediaIds).size() != orderedMediaIds.size()) {
            throw new InvalidRentalPropertyMediaException(
                    "Media order must contain every property media id exactly once"
            );
        }
        var byId = new HashMap<Long, RentalPropertyMedia>();
        current.forEach(item -> byId.put(item.getId(), item));
        for (int index = 0; index < orderedMediaIds.size(); index++) {
            RentalPropertyMedia media = byId.get(orderedMediaIds.get(index));
            if (media == null) {
                throw new InvalidRentalPropertyMediaException(
                        "Media order contains an id from another property"
                );
            }
            media.reorder(index);
        }
        property.touch(clock.instant());
    }

    @Transactional
    public void deleteAllForProperty(long propertyId) {
        List<RentalPropertyMedia> propertyMedia = media(propertyId);
        List<Long> assetIds = propertyMedia.stream()
                .flatMap(item -> mediaAssetIds(item).stream())
                .distinct()
                .toList();
        mediaRepository.deleteAll(propertyMedia);
        mediaRepository.flush();
        assetIds.forEach(this::deleteAssetIfUnreferenced);
    }

    @Transactional(readOnly = true)
    public RentalMediaContent getAdminContent(long propertyId, long mediaId) {
        return getAdminContent(propertyId, mediaId, RentalMediaVariant.FULL);
    }

    @Transactional(readOnly = true)
    public RentalMediaContent getAdminContent(
            long propertyId,
            long mediaId,
            RentalMediaVariant variant
    ) {
        return content(requireMedia(propertyId, mediaId), variant);
    }

    @Transactional(readOnly = true)
    public RentalMediaContent getPublicContent(long propertyId, long mediaId) {
        return getPublicContent(propertyId, mediaId, RentalMediaVariant.FULL);
    }

    @Transactional(readOnly = true)
    public RentalMediaContent getPublicContent(
            long propertyId,
            long mediaId,
            RentalMediaVariant variant
    ) {
        RentalPropertyMedia media = mediaRepository
                .findByIdAndProperty_IdAndProperty_Status(
                        mediaId,
                        propertyId,
                        RentalPropertyStatus.PUBLISHED
                )
                .orElseThrow(() -> new RentalPropertyMediaNotFoundException(propertyId, mediaId));
        return content(media, variant);
    }

    private RentalMediaContent content(RentalPropertyMedia media, RentalMediaVariant variant) {
        var content = mediaStorage.get(media.mediaAssetId(variant));
        return new RentalMediaContent(content.contentType(), content.content());
    }

    private void deleteAssetIfUnreferenced(long assetId) {
        if (!mediaRepository.existsByMediaAssetIdOrCardMediaAssetIdOrThumbnailMediaAssetId(
                assetId,
                assetId,
                assetId
        )) {
            mediaStorage.delete(assetId);
        }
    }

    private static List<Long> mediaAssetIds(RentalPropertyMedia media) {
        var assetIds = new java.util.ArrayList<Long>(3);
        assetIds.add(media.getMediaAssetId());
        if (media.getCardMediaAssetId() != null) {
            assetIds.add(media.getCardMediaAssetId());
        }
        if (media.getThumbnailMediaAssetId() != null) {
            assetIds.add(media.getThumbnailMediaAssetId());
        }
        return assetIds;
    }

    private RentalProperty requireProperty(long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RentalPropertyNotFoundException(propertyId));
    }

    private RentalPropertyMedia requireMedia(long propertyId, long mediaId) {
        return mediaRepository.findByIdAndProperty_Id(mediaId, propertyId)
                .orElseThrow(() -> new RentalPropertyMediaNotFoundException(propertyId, mediaId));
    }

    private List<RentalPropertyMedia> media(long propertyId) {
        return mediaRepository.findAllByProperty_IdOrderBySortOrderAscIdAsc(propertyId);
    }
}
