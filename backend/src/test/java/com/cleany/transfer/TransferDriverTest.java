package com.cleany.transfer;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TransferDriverTest {

    private static final Instant NOW = Instant.parse("2026-08-29T12:00:00Z");

    @Test
    void telegramAuthorizationRequiresConfiguredIdentityAndIsClearedWhenIdentityChanges() {
        TransferDriver driver = new TransferDriver(
                "Driver",
                "+905551112233",
                true,
                1001L,
                NOW
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        DriverTelegramStatus.AWAITING_AUTHORIZATION,
                        driver.telegramStatus()
                ),
                () -> Assertions.assertThrows(
                        InvalidTransferConfigurationException.class,
                        () -> driver.authorizeTelegram(1002L, 1002L, NOW.plusSeconds(1))
                )
        );

        driver.authorizeTelegram(1001L, 1001L, NOW.plusSeconds(1));
        Assertions.assertTrue(driver.canReceiveTelegramBookings());

        driver.update("Driver", "+905551112233", true, 1002L, NOW.plusSeconds(2));
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        DriverTelegramStatus.AWAITING_AUTHORIZATION,
                        driver.telegramStatus()
                ),
                () -> Assertions.assertFalse(driver.isTelegramNotificationsEnabled()),
                () -> Assertions.assertNull(driver.getVerifiedTelegramUserId()),
                () -> Assertions.assertNull(driver.getTelegramChatId())
        );
    }
}
