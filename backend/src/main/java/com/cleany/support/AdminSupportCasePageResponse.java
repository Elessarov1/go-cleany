package com.cleany.support;

import java.util.List;

public record AdminSupportCasePageResponse(
        List<AdminSupportCaseSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
