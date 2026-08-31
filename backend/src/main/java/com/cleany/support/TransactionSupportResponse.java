package com.cleany.support;

import com.cleany.catalog.PlatformService;

public record TransactionSupportResponse(
        PlatformService service,
        long sourceEntityId,
        boolean feedbackEligible,
        TransactionFeedbackResponse feedback,
        SupportCaseResponse latestCase
) {
}
