package com.cleany.communication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterCommunicationEndpointRequest(
        @NotNull CommunicationPlatform platform,
        @NotBlank @Size(max = 4_096) String registrationToken,
        @NotBlank @Size(max = 128) String installationId,
        @Size(max = 16) String locale,
        @Size(max = 32) String appVersion
) {
}
