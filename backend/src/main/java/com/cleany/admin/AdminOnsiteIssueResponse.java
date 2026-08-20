package com.cleany.admin;

import java.time.Instant;
import java.util.List;

import com.cleany.order.CleaningOrderIssuePhotoMetadata;
import com.cleany.order.CleaningOrderIssueReport;
import com.cleany.order.OnsiteIssueReason;

public record AdminOnsiteIssueResponse(
        long id,
        OnsiteIssueReason reason,
        long cleanerTelegramUserId,
        Instant reportedAt,
        String comment,
        List<AdminIssuePhotoMetadataResponse> photos,
        Instant resolvedAt,
        Long resolvedBy,
        String resolutionComment
) {

    static AdminOnsiteIssueResponse from(
            CleaningOrderIssueReport report,
            List<CleaningOrderIssuePhotoMetadata> photos
    ) {
        return new AdminOnsiteIssueResponse(
                report.getId(),
                report.getReason(),
                report.getCleanerTelegramUserId(),
                report.getSubmittedAt(),
                report.getComment(),
                photos.stream().map(AdminIssuePhotoMetadataResponse::from).toList(),
                report.getResolvedAt(),
                report.getResolvedBy(),
                report.getResolutionComment()
        );
    }
}
