package com.cleany.rental;

import jakarta.validation.constraints.NotNull;

public record UpdateRentalAdminNotificationPreferenceRequest(
        @NotNull Boolean telegramEnabled
) {
}
