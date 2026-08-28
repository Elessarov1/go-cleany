package com.cleany.catalog;

import java.util.EnumSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cleany.authorization.CustomerRoleRepository;
import com.cleany.authorization.PlatformRole;
import com.cleany.authorization.PlatformRoleService;
import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.ExternalIdentityProvider;

class PlatformServiceAccessIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PlatformServiceStateRepository stateRepository;

    @Autowired
    private PlatformServiceAccessService accessService;

    @Autowired
    private CustomerAccountService customerAccountService;

    @Autowired
    private CustomerRoleRepository roleRepository;

    @Autowired
    private PlatformRoleService roleService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void resetPlatformState() {
        jdbcTemplate.update("delete from customer_role");
        jdbcTemplate.update("""
                update platform_service_state
                   set status = 'ENABLED',
                       updated_at = current_timestamp,
                       updated_by_customer_id = null,
                       version = version + 1
                """);
    }

    @Test
    void migrationSeedsBothRealServicesAsEnabled() {
        var states = stateRepository.findAll();

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, states.size()),
                () -> Assertions.assertEquals(
                        EnumSet.allOf(PlatformService.class),
                        states.stream()
                                .map(PlatformServiceState::getService)
                                .collect(java.util.stream.Collectors.toSet())
                ),
                () -> Assertions.assertTrue(states.stream().allMatch(
                        state -> state.getStatus() == PlatformServiceStatus.ENABLED
                ))
        );
    }

    @Test
    void enabledInTestAndDisabledFollowExactCustomerSemantics() {
        CurrentCustomer ordinary = customer(
                ExternalIdentityProvider.GOOGLE,
                "905551111111"
        );
        CurrentCustomer admin = customer(
                ExternalIdentityProvider.TELEGRAM,
                "900001"
        );
        roleService.ensureRole(admin.customerId(), PlatformRole.ADMIN);
        PlatformServiceState cleaning = stateRepository.findById(PlatformService.CLEANING)
                .orElseThrow();

        Assertions.assertAll(
                () -> Assertions.assertTrue(accessService.canStartCustomerFlow(
                        PlatformService.CLEANING,
                        ordinary.customerId()
                )),
                () -> Assertions.assertTrue(roleRepository.existsByCustomerIdAndRole(
                        admin.customerId(),
                        PlatformRole.ADMIN
                ))
        );

        cleaning.changeStatus(PlatformServiceStatus.IN_TEST, admin.customerId(), java.time.Instant.now());
        cleaning = stateRepository.saveAndFlush(cleaning);
        Assertions.assertAll(
                () -> Assertions.assertFalse(accessService.canStartCustomerFlow(
                        PlatformService.CLEANING,
                        ordinary.customerId()
                )),
                () -> Assertions.assertTrue(accessService.canStartCustomerFlow(
                        PlatformService.CLEANING,
                        admin.customerId()
                ))
        );

        cleaning.changeStatus(PlatformServiceStatus.DISABLED, admin.customerId(), java.time.Instant.now());
        stateRepository.saveAndFlush(cleaning);
        Assertions.assertAll(
                () -> Assertions.assertFalse(accessService.canStartCustomerFlow(
                        PlatformService.CLEANING,
                        ordinary.customerId()
                )),
                () -> Assertions.assertFalse(accessService.canStartCustomerFlow(
                        PlatformService.CLEANING,
                        admin.customerId()
                )),
                () -> Assertions.assertThrows(
                        PlatformServiceNotAvailableException.class,
                        () -> accessService.requireCanStartCustomerFlow(
                                PlatformService.CLEANING,
                                admin.customerId()
                        )
                )
        );
    }

    @Test
    void telegramIdentityNeverBootstrapsAdminFromConfiguration() {
        CurrentCustomer telegramAdmin = customer(ExternalIdentityProvider.TELEGRAM, "900001");
        customer(ExternalIdentityProvider.TELEGRAM, "900001");
        CurrentCustomer sameSubjectOtherProvider = customer(
                ExternalIdentityProvider.GOOGLE,
                "900001"
        );

        Assertions.assertAll(
                () -> Assertions.assertFalse(roleRepository.existsByCustomerIdAndRole(
                        telegramAdmin.customerId(),
                        PlatformRole.ADMIN
                )),
                () -> Assertions.assertFalse(roleRepository.existsByCustomerIdAndRole(
                        sameSubjectOtherProvider.customerId(),
                        PlatformRole.ADMIN
                )),
                () -> Assertions.assertEquals(0L, roleRepository.count())
        );
    }

    private CurrentCustomer customer(ExternalIdentityProvider provider, String subject) {
        return customerAccountService.resolveCustomer(new AuthenticatedCustomerIdentity(
                provider,
                subject,
                null,
                "Customer " + subject,
                "ru"
        ));
    }
}
