package com.cleany.customer;

import jakarta.validation.constraints.Size;

public record ConfirmIdentityLinkRequest(
        @Size(max = 16_384) String identityToken,
        @Size(max = 16_384) String telegramInitData
) {
}
