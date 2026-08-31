package com.cleany.support;

import java.time.Instant;

public record TransactionFeedbackResponse(
        long id,
        FeedbackOutcome outcome,
        SupportCaseCategory category,
        String comment,
        Long supportCaseId,
        Instant createdAt
) {

    static TransactionFeedbackResponse from(TransactionFeedback feedback) {
        return new TransactionFeedbackResponse(
                feedback.getId(),
                feedback.getOutcome(),
                feedback.getCategory(),
                feedback.getComment(),
                feedback.getSupportCase() == null ? null : feedback.getSupportCase().getId(),
                feedback.getCreatedAt()
        );
    }
}
