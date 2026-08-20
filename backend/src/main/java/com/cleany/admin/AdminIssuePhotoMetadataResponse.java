package com.cleany.admin;

import java.time.Instant;

import com.cleany.order.CleaningOrderIssuePhotoMetadata;

public record AdminIssuePhotoMetadataResponse(
        long id,
        String contentType,
        long sizeBytes,
        String sha256,
        Instant createdAt
) {

    static AdminIssuePhotoMetadataResponse from(CleaningOrderIssuePhotoMetadata photo) {
        return new AdminIssuePhotoMetadataResponse(
                photo.id(),
                photo.contentType(),
                photo.sizeBytes(),
                photo.sha256(),
                photo.createdAt()
        );
    }
}
