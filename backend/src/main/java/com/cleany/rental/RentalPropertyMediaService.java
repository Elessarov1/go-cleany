package com.cleany.rental;

import java.time.Clock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.media.ImageMediaTypeDetector;
import com.cleany.media.MediaStorage;
import com.cleany.media.MediaUpload;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalPropertyMediaService {

    private static final int MAX_IMAGE_BYTES = 10 * 1024 * 1024;

    private final RentalPropertyRepository propertyRepository;
    private final RentalPropertyMediaRepository mediaRepository;
    private final MediaStorage mediaStorage;
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
        String contentType = ImageMediaTypeDetector.detect(content)
                .orElseThrow(() -> new InvalidRentalPropertyMediaException(
                        "Property image must be JPEG or PNG"
                ));
        List<RentalPropertyMedia> existing = media(propertyId);
        boolean cover = requestedCover || existing.isEmpty();
        if (cover) {
            existing.forEach(item -> item.setCover(false));
            mediaRepository.flush();
        }
        var stored = mediaStorage.store(new MediaUpload(content, contentType));
        mediaRepository.save(new RentalPropertyMedia(
                property,
                stored.mediaId(),
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
        long assetId = media.getMediaAssetId();
        mediaRepository.delete(media);
        mediaRepository.flush();
        mediaStorage.delete(assetId);
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

    @Transactional(readOnly = true)
    public RentalMediaContent getAdminContent(long propertyId, long mediaId) {
        return content(requireMedia(propertyId, mediaId));
    }

    @Transactional(readOnly = true)
    public RentalMediaContent getPublicContent(long propertyId, long mediaId) {
        RentalPropertyMedia media = mediaRepository
                .findByIdAndProperty_IdAndProperty_Status(
                        mediaId,
                        propertyId,
                        RentalPropertyStatus.PUBLISHED
                )
                .orElseThrow(() -> new RentalPropertyMediaNotFoundException(propertyId, mediaId));
        return content(media);
    }

    private RentalMediaContent content(RentalPropertyMedia media) {
        var content = mediaStorage.get(media.getMediaAssetId());
        return new RentalMediaContent(content.contentType(), content.content());
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
