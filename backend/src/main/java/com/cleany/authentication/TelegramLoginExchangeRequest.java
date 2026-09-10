package com.cleany.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TelegramLoginExchangeRequest(
        @NotBlank String verifier,
        @NotNull NativeClientType clientType
) {
}
