package com.cleany.configuration;

import java.util.List;
import java.util.Locale;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "admin")
public record AdminProperties(List<Long> telegramIds, List<String> googleEmails) {

    public AdminProperties {
        telegramIds = telegramIds == null
                ? List.of()
                : telegramIds.stream().distinct().toList();
        if (telegramIds.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new IllegalArgumentException("admin.telegram-ids must contain only positive values");
        }
        googleEmails = googleEmails == null
                ? List.of()
                : googleEmails.stream()
                        .filter(email -> email != null && !email.isBlank())
                        .map(email -> email.trim().toLowerCase(Locale.ROOT))
                        .distinct()
                        .toList();
    }

    public boolean contains(long telegramId) {
        return telegramIds.contains(telegramId);
    }

    public boolean containsGoogleEmail(String email) {
        return email != null && googleEmails.contains(email.trim().toLowerCase(Locale.ROOT));
    }
}
