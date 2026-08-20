package com.cleany.configuration;

import java.util.Locale;
import java.util.Set;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "onsite-issue")
public record OnsiteIssueProperties(
        @Min(1) @Max(8) int minPhotos,
        @Min(1) @Max(8) int maxPhotos,
        @NotNull DataSize maxPhotoSize,
        @NotEmpty Set<String> supportedContentTypes
) {

    public OnsiteIssueProperties {
        if (minPhotos > maxPhotos) {
            throw new IllegalArgumentException("onsite-issue.min-photos must not exceed max-photos");
        }
        if (maxPhotoSize == null || maxPhotoSize.toBytes() <= 0) {
            throw new IllegalArgumentException("onsite-issue.max-photo-size must be positive");
        }
        supportedContentTypes = supportedContentTypes == null
                ? Set.of()
                : supportedContentTypes.stream()
                        .map(value -> value.toLowerCase(Locale.ROOT).trim())
                        .filter(value -> !value.isEmpty())
                        .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }
}
