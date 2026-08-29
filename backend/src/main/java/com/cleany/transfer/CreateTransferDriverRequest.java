package com.cleany.transfer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateTransferDriverRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 40) String phone,
        boolean enabled,
        @Positive Long telegramUserId
) {
}
