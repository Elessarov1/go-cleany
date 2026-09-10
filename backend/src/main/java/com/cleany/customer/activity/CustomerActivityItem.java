package com.cleany.customer.activity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import com.cleany.action.ActionTarget;
import com.cleany.catalog.PlatformService;
import com.cleany.configuration.Money;

public record CustomerActivityItem(
        PlatformService service,
        long entityId,
        String status,
        String titleRu,
        String titleEn,
        String subtitleRu,
        String subtitleEn,
        LocalDate scheduledDate,
        LocalDate scheduledEndDate,
        LocalTime scheduledTime,
        Instant occurredAt,
        Money money,
        ActionTarget action
) {
}
