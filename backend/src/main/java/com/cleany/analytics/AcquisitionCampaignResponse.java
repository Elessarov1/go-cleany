package com.cleany.analytics;

import java.time.Instant;

public record AcquisitionCampaignResponse(
        long id,
        String publicCode,
        String name,
        AcquisitionChannel channel,
        AcquisitionMedium medium,
        AcquisitionTargetService targetService,
        Long partnerId,
        String partnerName,
        boolean active,
        Instant createdAt,
        Instant disabledAt,
        String trackingPath,
        String targetPath
) {
}
