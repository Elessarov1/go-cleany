package com.cleany.transfer;

import java.time.Instant;

public record AdminTransferDriverResponse(
        long id,
        String name,
        String phone,
        boolean enabled,
        Long configuredTelegramUserId,
        Long verifiedTelegramUserId,
        Long telegramChatId,
        boolean telegramNotificationsEnabled,
        Instant telegramBotAuthorizedAt,
        DriverTelegramStatus telegramStatus,
        Instant createdAt,
        Instant updatedAt,
        long version
) {

    static AdminTransferDriverResponse from(TransferDriver driver) {
        return new AdminTransferDriverResponse(
                driver.getId(), driver.getName(), driver.getPhone(), driver.isEnabled(),
                driver.getConfiguredTelegramUserId(), driver.getVerifiedTelegramUserId(),
                driver.getTelegramChatId(), driver.isTelegramNotificationsEnabled(),
                driver.getTelegramBotAuthorizedAt(), driver.telegramStatus(), driver.getCreatedAt(),
                driver.getUpdatedAt(), driver.getVersion()
        );
    }
}
