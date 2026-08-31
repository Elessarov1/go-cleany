package com.cleany.customer.home;

import com.cleany.customer.activity.CustomerActivityItem;

public record CustomerHomeResponse(
        boolean hasActivity,
        CustomerActivityItem activeTransaction,
        int activeTransactionCount,
        CustomerHomePrimaryAction primaryAction,
        CustomerHomeRepeatOpportunity repeatOpportunity
) {
}
