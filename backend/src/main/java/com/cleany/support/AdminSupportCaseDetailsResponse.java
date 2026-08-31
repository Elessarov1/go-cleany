package com.cleany.support;

import java.time.Instant;

public record AdminSupportCaseDetailsResponse(
        AdminSupportCaseSummaryResponse summary,
        String description,
        Long resolvedByCustomerId,
        String resolutionComment,
        Instant resolvedAt,
        String sourceCustomerPath
) {

    static AdminSupportCaseDetailsResponse from(SupportCase supportCase, SupportSource source) {
        return new AdminSupportCaseDetailsResponse(
                AdminSupportCaseSummaryResponse.from(supportCase, source),
                supportCase.getDescription(),
                supportCase.getResolvedByCustomerId(),
                supportCase.getResolutionComment(),
                supportCase.getResolvedAt(),
                source.customerPath()
        );
    }
}
