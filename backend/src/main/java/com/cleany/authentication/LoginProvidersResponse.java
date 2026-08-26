package com.cleany.authentication;

import com.cleany.configuration.GoogleOidcProperties;

public record LoginProvidersResponse(LoginProviderAvailability google) {

    static LoginProvidersResponse from(GoogleOidcProperties properties) {
        return new LoginProvidersResponse(new LoginProviderAvailability(properties.enabled()));
    }

    public record LoginProviderAvailability(boolean available) {
    }
}
