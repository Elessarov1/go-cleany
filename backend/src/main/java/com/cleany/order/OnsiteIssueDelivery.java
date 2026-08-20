package com.cleany.order;

import java.util.List;

public record OnsiteIssueDelivery(
        CleaningOrder order,
        OnsiteIssueReason reason,
        String comment,
        List<String> telegramFileIds
) {

    public OnsiteIssueDelivery {
        telegramFileIds = List.copyOf(telegramFileIds);
    }
}
