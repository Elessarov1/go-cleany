package com.cleany.customer.activity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import com.cleany.catalog.PlatformService;

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
        BigDecimal amount,
        String currency,
        String targetPath
) {
}
