package com.cleany.customer;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SensitiveAccountActionRequest(
        @NotNull UUID challengeId,
        @Size(max = 16_384) String identityToken,
        @Size(max = 16_384) String telegramInitData
) {
}
