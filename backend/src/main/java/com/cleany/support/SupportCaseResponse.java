package com.cleany.support;

import java.time.Instant;

import com.cleany.catalog.PlatformService;

public record SupportCaseResponse(
        long id,
        PlatformService service,
        long sourceEntityId,
        SupportCaseCategory category,
        SupportCaseStatus status,
        String description,
        Instant createdAt,
        Instant resolvedAt,
        String resolutionComment
) {

    static SupportCaseResponse from(SupportCase supportCase) {
        return new SupportCaseResponse(
                supportCase.getId(),
                supportCase.getService(),
                supportCase.getSourceEntityId(),
                supportCase.getCategory(),
                supportCase.getStatus(),
                supportCase.getDescription(),
                supportCase.getCreatedAt(),
                supportCase.getResolvedAt(),
                supportCase.getResolutionComment()
        );
    }
}
