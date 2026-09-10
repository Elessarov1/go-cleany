package com.cleany.configuration;

public record ClientConfigurationResponse(String environment, String iosApplicationId,
                                          String androidApplicationId, String minimumIosVersion,
                                          String minimumAndroidVersion, String apiRevision) {
    static ClientConfigurationResponse from(ClientConfigurationProperties properties) {
        return new ClientConfigurationResponse(properties.environment(), properties.iosApplicationId(),
                properties.androidApplicationId(), properties.minimumIosVersion(),
                properties.minimumAndroidVersion(), properties.apiRevision());
    }
}
