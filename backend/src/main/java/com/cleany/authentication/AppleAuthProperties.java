package com.cleany.authentication;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("native-auth.apple")
public record AppleAuthProperties(String teamId, String keyId, String clientId, String privateKey,
                                  String tokenUri, String revokeUri) {
    public AppleAuthProperties {
        teamId = normalize(teamId);
        keyId = normalize(keyId);
        clientId = normalize(clientId);
        privateKey = normalize(privateKey);
        tokenUri = normalize(tokenUri) == null ? "https://appleid.apple.com/auth/token" : tokenUri.trim();
        revokeUri = normalize(revokeUri) == null ? "https://appleid.apple.com/auth/revoke" : revokeUri.trim();
    }

    public void requireConfigured() {
        if (teamId == null || keyId == null || clientId == null || privateKey == null) {
            throw new NativeAuthenticationException("apple_provider_unconfigured",
                    "Apple authentication is not configured");
        }
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
