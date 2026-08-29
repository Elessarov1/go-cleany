package com.cleany.configuration;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "admin")
public record AdminProperties(List<String> googleEmails) {

    public AdminProperties {
        googleEmails = googleEmails == null
                ? Collections.emptyList()
                : googleEmails.stream()
                        .filter(email -> email != null && !email.isBlank())
                        .map(email -> email.trim().toLowerCase(Locale.ROOT))
                        .distinct()
                        .toList();
    }

    public boolean containsGoogleEmail(String email) {
        return email != null && googleEmails.contains(email.trim().toLowerCase(Locale.ROOT));
    }
}
