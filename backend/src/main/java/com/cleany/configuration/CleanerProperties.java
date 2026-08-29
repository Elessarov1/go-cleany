package com.cleany.configuration;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cleaner")
public record CleanerProperties(List<Long> telegramIds) {

    public CleanerProperties {
        telegramIds = telegramIds == null ? Collections.emptyList() : List.copyOf(telegramIds);
    }

    public boolean contains(long telegramUserId) {
        return telegramIds.contains(telegramUserId);
    }
}
