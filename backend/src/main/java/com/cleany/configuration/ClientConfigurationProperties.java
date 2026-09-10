package com.cleany.configuration;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("client-configuration")
public record ClientConfigurationProperties(
        String environment,
        String iosApplicationId,
        String androidApplicationId,
        String appleTeamId,
        List<String> androidSha256Fingerprints,
        String minimumIosVersion,
        String minimumAndroidVersion,
        String apiRevision
) {
    public ClientConfigurationProperties {
        environment = normalize(environment, "production");
        iosApplicationId = normalize(iosApplicationId, "com.locoplace.app");
        androidApplicationId = normalize(androidApplicationId, "com.locoplace.app");
        appleTeamId = normalize(appleTeamId, null);
        androidSha256Fingerprints = androidSha256Fingerprints == null
                ? Collections.emptyList()
                : androidSha256Fingerprints.stream().filter(value -> value != null && !value.isBlank())
                        .map(String::trim).toList();
        minimumIosVersion = normalize(minimumIosVersion, "1.0.0");
        minimumAndroidVersion = normalize(minimumAndroidVersion, "1.0.0");
        apiRevision = normalize(apiRevision, "development");
    }

    private static String normalize(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
