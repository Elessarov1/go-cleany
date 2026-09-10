package com.cleany.authentication;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NativeProviderLoginRequest(
        @NotNull UUID challengeId,
        @NotNull NativeClientType clientType,
        @NotBlank String identityToken,
        String authorizationCode
) {
}
