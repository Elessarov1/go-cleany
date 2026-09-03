package com.cleany.rental;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.media.MediaStorage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class RentalMediaBackfillBatchProcessor {

    private final RentalPropertyMediaRepository mediaRepository;
    private final MediaStorage mediaStorage;
    private final RentalImageNormalizer imageNormalizer;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    int processNextBatch(int batchSize) {
        var mediaItems = mediaRepository.lockNextMissingVariants(batchSize);
        Set<Long> changedPropertyIds = new HashSet<>();
        for (RentalPropertyMedia media : mediaItems) {
            long propertyId = media.getProperty().getId();
            try {
                var source = mediaStorage.get(media.getMediaAssetId());
                RentalImageVariants variants = imageNormalizer.normalize(source.content());
                Long cardAssetId = media.getCardMediaAssetId() == null
                        ? mediaStorage.store(variants.card()).mediaId()
                        : null;
                Long thumbnailAssetId = media.getThumbnailMediaAssetId() == null
                        ? mediaStorage.store(variants.thumbnail()).mediaId()
                        : null;
                media.attachMissingVariants(cardAssetId, thumbnailAssetId);
                changedPropertyIds.add(propertyId);
            } catch (RuntimeException exception) {
                throw new RentalMediaBackfillException(propertyId, media.getId(), exception);
            }
        }
        mediaRepository.flush();
        changedPropertyIds.forEach(propertyId -> eventPublisher.publishEvent(
                new RentalPropertyMediaChangedEvent(propertyId)
        ));
        return mediaItems.size();
    }
}
