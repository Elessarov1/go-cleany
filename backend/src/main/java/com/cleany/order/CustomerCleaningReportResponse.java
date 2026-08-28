package com.cleany.order;

import java.time.Instant;
import java.util.List;

public record CustomerCleaningReportResponse(
        CleaningReportStatus status,
        Instant expiresAt,
        int retentionDays,
        String cleanerComment,
        List<CustomerCleaningReportPhotoResponse> photos
) {

    public CustomerCleaningReportResponse {
        photos = List.copyOf(photos);
    }
}
