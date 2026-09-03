package com.cleany.rental;

public record RentalPropertyMediaResponse(
        long id,
        long mediaAssetId,
        int sortOrder,
        boolean cover,
        String url,
        String cardUrl,
        String thumbnailUrl
) {

    static RentalPropertyMediaResponse from(RentalPropertyMedia media, String url) {
        return new RentalPropertyMediaResponse(
                media.getId(),
                media.getMediaAssetId(),
                media.getSortOrder(),
                media.isCover(),
                versioned(url, media.mediaAssetId(RentalMediaVariant.FULL)),
                versioned(url + "/card", media.mediaAssetId(RentalMediaVariant.CARD)),
                versioned(
                        url + "/thumbnail",
                        media.mediaAssetId(RentalMediaVariant.THUMBNAIL)
                )
        );
    }

    private static String versioned(String url, long mediaAssetId) {
        return url + "?v=" + mediaAssetId;
    }
}
