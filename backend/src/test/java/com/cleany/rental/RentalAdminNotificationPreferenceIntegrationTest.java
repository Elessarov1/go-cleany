package com.cleany.rental;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cleany.base.BaseIntegrationTest;

class RentalAdminNotificationPreferenceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RentalAdminNotificationPreferenceRepository repository;

    @Autowired
    private RentalAdminNotificationPreferenceService service;

    @BeforeEach
    @AfterEach
    void cleanPreferences() {
        repository.deleteAll();
    }

    @Test
    void liquibaseSchema_persistsIndependentPreferencesAndMissingAdminDefaultsToEnabled() {
        repository.saveAllAndFlush(List.of(
                new RentalAdminNotificationPreference(
                        1001L,
                        false,
                        Instant.parse("2026-08-23T10:00:00Z")
                ),
                new RentalAdminNotificationPreference(
                        1002L,
                        true,
                        Instant.parse("2026-08-23T10:01:00Z")
                )
        ));

        Assertions.assertAll(
                () -> Assertions.assertFalse(
                        repository.findById(1001L).orElseThrow().isTelegramEnabled()
                ),
                () -> Assertions.assertTrue(
                        repository.findById(1002L).orElseThrow().isTelegramEnabled()
                ),
                () -> Assertions.assertEquals(
                        List.of(1002L, 1003L),
                        service.enabledAdminIds(List.of(1001L, 1002L, 1003L))
                )
        );
    }
}
