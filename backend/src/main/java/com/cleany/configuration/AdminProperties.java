package com.cleany.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "admin")
public record AdminProperties(List<Long> telegramIds) {

    public AdminProperties {
        telegramIds = telegramIds == null
                ? List.of()
                : telegramIds.stream().distinct().toList();
        if (telegramIds.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new IllegalArgumentException("admin.telegram-ids must contain only positive values");
        }
    }

    public boolean contains(long telegramId) {
        return telegramIds.contains(telegramId);
    }
}
