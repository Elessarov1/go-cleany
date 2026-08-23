package com.cleany.rental;

public record RentalPropertyMediaResponse(
        long id,
        long mediaAssetId,
        int sortOrder,
        boolean cover,
        String url
) {

    static RentalPropertyMediaResponse from(RentalPropertyMedia media, String url) {
        return new RentalPropertyMediaResponse(
                media.getId(),
                media.getMediaAssetId(),
                media.getSortOrder(),
                media.isCover(),
                url
        );
    }
}
