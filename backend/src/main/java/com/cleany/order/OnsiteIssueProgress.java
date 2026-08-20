package com.cleany.order;

public record OnsiteIssueProgress(
        long orderId,
        OnsiteIssueReason reason,
        long photoCount,
        boolean commentPresent,
        boolean readyToSubmit
) {
}
