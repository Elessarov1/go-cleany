package com.cleany.authentication;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cleany.authorization.CustomerRoleRepository;
import com.cleany.authorization.PlatformRole;
import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;

class GoogleIdentityIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CustomerAccountService customerAccountService;

    @Autowired
    private CustomerExternalIdentityRepository identityRepository;

    @Autowired
    private CustomerRoleRepository roleRepository;

    @Autowired
    private CustomerAccountRepository accountRepository;

    @BeforeEach
    @AfterEach
    void cleanIdentityData() {
        roleRepository.deleteAll();
        identityRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void sameGoogleSubjectResolvesSameAccountAndVerifiedAllowlistGrantsAdminIdempotently() {
        var identity = google("google-sub-1", "ADMIN@example.test", true, "Alex Admin");

        var first = customerAccountService.resolveCustomer(identity);
        var second = customerAccountService.resolveCustomer(identity);
        var persisted = identityRepository.findByProviderAndExternalSubject(
                ExternalIdentityProvider.GOOGLE,
                "google-sub-1"
        ).orElseThrow();

        Assertions.assertAll(
                () -> Assertions.assertEquals(first.customerId(), second.customerId()),
                () -> Assertions.assertEquals("admin@example.test", persisted.getEmail()),
                () -> Assertions.assertTrue(persisted.isEmailVerified()),
                () -> Assertions.assertTrue(roleRepository.existsByCustomerIdAndRole(
                        first.customerId(),
                        PlatformRole.ADMIN
                )),
                () -> Assertions.assertEquals(1L, roleRepository.count())
        );
    }

    @Test
    void unverifiedAllowlistedEmailNeverGrantsAdmin() {
        var customer = customerAccountService.resolveCustomer(
                google("google-sub-2", "admin@example.test", false, "Alex")
        );

        Assertions.assertFalse(roleRepository.existsByCustomerIdAndRole(
                customer.customerId(),
                PlatformRole.ADMIN
        ));
    }

    @Test
    void telegramAndGoogleAreNeverAutomaticallyMergedByMatchingProfile() {
        var telegram = customerAccountService.resolveCustomer(new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.TELEGRAM,
                "555001",
                "alex",
                "Alex Same Person",
                "ru"
        ));
        var google = customerAccountService.resolveCustomer(
                google("google-sub-3", "alex@example.test", true, "Alex Same Person")
        );

        Assertions.assertNotEquals(telegram.customerId(), google.customerId());
        Assertions.assertEquals(2L, accountRepository.count());
    }

    @Test
    void differentGoogleSubjectsNeverCollideEvenWithMatchingProfiles() {
        var first = customerAccountService.resolveCustomer(
                google("google-sub-4", "same@example.test", true, "Same Person")
        );
        var second = customerAccountService.resolveCustomer(
                google("google-sub-5", "same@example.test", true, "Same Person")
        );

        Assertions.assertNotEquals(first.customerId(), second.customerId());
        Assertions.assertEquals(2L, accountRepository.count());
    }

    private static AuthenticatedCustomerIdentity google(
            String subject,
            String email,
            boolean verified,
            String displayName
    ) {
        return new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.GOOGLE,
                subject,
                email,
                displayName,
                "en",
                email,
                verified
        );
    }
}
