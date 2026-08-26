package com.cleany.configuration;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AdminPropertiesTest {

    @Test
    void telegramIds_duplicatesRemovedAndMembershipChecked() {
        AdminProperties properties = new AdminProperties(List.of(10L, 10L, 20L), List.of());

        Assertions.assertAll(
                () -> Assertions.assertEquals(List.of(10L, 20L), properties.telegramIds()),
                () -> Assertions.assertTrue(properties.contains(10L)),
                () -> Assertions.assertFalse(properties.contains(30L))
        );
    }

    @Test
    void telegramIds_nonPositiveValue_rejected() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new AdminProperties(List.of(0L), List.of())
        );
    }
}
