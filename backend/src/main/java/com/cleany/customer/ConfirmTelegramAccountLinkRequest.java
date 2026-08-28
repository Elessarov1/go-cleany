package com.cleany.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConfirmTelegramAccountLinkRequest(
        @NotBlank @Size(max = 256) String token
) {
}
