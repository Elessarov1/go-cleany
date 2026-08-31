package com.cleany.support;

import java.time.Instant;

import com.cleany.catalog.PlatformService;

public record AdminSupportCaseSummaryResponse(
        long id,
        PlatformService service,
        long sourceEntityId,
        long customerId,
        String customerName,
        String customerPhone,
        SupportCaseCategory category,
        SupportCaseStatus status,
        Instant createdAt,
        Instant resolvedAt,
        String sourceAdminPath
) {

    static AdminSupportCaseSummaryResponse from(SupportCase supportCase, SupportSource source) {
        return new AdminSupportCaseSummaryResponse(
                supportCase.getId(),
                supportCase.getService(),
                supportCase.getSourceEntityId(),
                supportCase.getCustomerId(),
                source.customerName(),
                source.customerPhone(),
                supportCase.getCategory(),
                supportCase.getStatus(),
                supportCase.getCreatedAt(),
                supportCase.getResolvedAt(),
                source.adminPath()
        );
    }
}
