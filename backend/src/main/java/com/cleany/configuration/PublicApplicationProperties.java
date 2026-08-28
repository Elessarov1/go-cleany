package com.cleany.configuration;

import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "public-application")
public record PublicApplicationProperties(@NotBlank String baseUrl) {

    public PublicApplicationProperties {
        baseUrl = baseUrl == null ? "" : baseUrl.strip().replaceAll("/+$", "");
    }
}
