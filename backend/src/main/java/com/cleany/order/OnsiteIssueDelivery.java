package com.cleany.order;

public record OnsiteIssueDelivery(
        CleaningOrder order,
        OnsiteIssueReason reason,
        String comment
) {
}
