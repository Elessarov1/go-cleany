package com.cleany.rental;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.media.MediaStorage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class RentalPublicMediaLoader {

    private final RentalPropertyMediaRepository mediaRepository;
    private final MediaStorage mediaStorage;

    @Transactional(readOnly = true)
    RentalMediaContent load(long propertyId, long mediaId, RentalMediaVariant variant) {
        RentalPropertyMedia media = mediaRepository
                .findByIdAndProperty_IdAndProperty_Status(
                        mediaId,
                        propertyId,
                        RentalPropertyStatus.PUBLISHED
                )
                .orElseThrow(() -> new RentalPropertyMediaNotFoundException(propertyId, mediaId));
        long assetId = media.mediaAssetId(variant);
        var content = mediaStorage.get(assetId);
        return new RentalMediaContent(assetId, content.contentType(), content.content());
    }
}
