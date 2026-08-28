package com.cleany.rental;

public record RentalAdminNotificationPreferenceResponse(
        boolean telegramLinked,
        boolean telegramEnabled,
        boolean writeAccessAllowed,
        String telegramUsername
) {
}
