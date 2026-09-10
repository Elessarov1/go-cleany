package com.cleany.communication;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fcm")
public record FcmProperties(boolean enabled, String projectId, String clientEmail,
                            String privateKey, String tokenUri, String scope) {
    public FcmProperties {
        projectId = normalize(projectId);
        clientEmail = normalize(clientEmail);
        privateKey = normalize(privateKey);
        tokenUri = normalize(tokenUri) == null ? "https://oauth2.googleapis.com/token" : tokenUri.trim();
        scope = normalize(scope) == null
                ? "https://www.googleapis.com/auth/firebase.messaging" : scope.trim();
        if (enabled && (projectId == null || clientEmail == null || privateKey == null)) {
            throw new IllegalArgumentException("FCM project id, client email and private key are required");
        }
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
