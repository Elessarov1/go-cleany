package com.cleany.rental;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CustomerAccount;
import com.cleany.customer.CustomerAccountRepository;

class RentalAdminNotificationPreferenceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RentalAdminNotificationPreferenceRepository repository;

    @Autowired
    private CustomerAccountRepository accountRepository;

    @BeforeEach
    @AfterEach
    void cleanPreferences() {
        repository.deleteAll();
    }

    @Test
    void liquibaseSchema_persistsIndependentPreferencesAndMissingAdminDefaultsToEnabled() {
        CustomerAccount first = accountRepository.save(new CustomerAccount(Instant.parse("2026-08-23T09:00:00Z")));
        CustomerAccount second = accountRepository.save(new CustomerAccount(Instant.parse("2026-08-23T09:01:00Z")));
        repository.saveAllAndFlush(List.of(
                new RentalAdminNotificationPreference(
                        first.getId(),
                        false,
                        Instant.parse("2026-08-23T10:00:00Z")
                ),
                new RentalAdminNotificationPreference(
                        second.getId(),
                        true,
                        Instant.parse("2026-08-23T10:01:00Z")
                )
        ));

        Assertions.assertAll(
                () -> Assertions.assertFalse(
                        repository.findById(first.getId()).orElseThrow().isTelegramEnabled()
                ),
                () -> Assertions.assertTrue(
                        repository.findById(second.getId()).orElseThrow().isTelegramEnabled()
                )
        );
    }
}
