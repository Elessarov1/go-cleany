package com.cleany.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "web-auth.google")
public record GoogleOidcProperties(
        boolean enabled,
        String clientId,
        String clientSecret
) {

    public GoogleOidcProperties {
        clientId = normalize(clientId);
        clientSecret = normalize(clientSecret);
        if (enabled && (clientId == null || clientSecret == null)) {
            throw new IllegalArgumentException(
                    "GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET are required when Google auth is enabled"
            );
        }
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
