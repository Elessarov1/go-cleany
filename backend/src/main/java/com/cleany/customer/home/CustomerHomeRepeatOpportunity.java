package com.cleany.customer.home;

import java.time.Instant;

import com.cleany.action.ActionTarget;
import com.cleany.catalog.PlatformService;

public record CustomerHomeRepeatOpportunity(
        PlatformService service,
        long sourceEntityId,
        Instant sourceCompletedAt,
        ActionTarget action
) {
}
