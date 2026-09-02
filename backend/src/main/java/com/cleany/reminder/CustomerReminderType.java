package com.cleany.reminder;

import com.cleany.catalog.PlatformService;

public enum CustomerReminderType {
    CLEANING_REPEAT(PlatformService.CLEANING),
    RENTAL_CHECKOUT_TRANSFER(PlatformService.RENTAL),
    TRANSFER_UPCOMING(PlatformService.TRANSFER);

    private final PlatformService sourceService;

    CustomerReminderType(PlatformService sourceService) {
        this.sourceService = sourceService;
    }

    public PlatformService sourceService() {
        return sourceService;
    }
}
