package com.cleany.rental;

record RentalSearchCoverRow(
        long propertyId,
        long mediaId,
        long version
) {

    String cardUrl() {
        return "/api/v1/rental/properties/" + propertyId
                + "/media/" + mediaId + "/card?v=" + version;
    }
}
