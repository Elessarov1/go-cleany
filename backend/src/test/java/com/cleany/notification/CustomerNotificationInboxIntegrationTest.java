package com.cleany.notification;

import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

import com.cleany.authorization.CustomerRoleRepository;
import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerAccountMergeService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.order.CleaningOrderCustomerNotification;
import com.cleany.telegram.TelegramInitDataTestFactory;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CustomerNotificationInboxIntegrationTest extends BaseIntegrationTest {

    private static final String BOT_TOKEN = "123456789:test-token";
    private static final long TELEGRAM_ID = 923456789L;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CustomerNotificationRecorder recorder;

    @Autowired
    private CustomerAccountService accountService;

    @Autowired
    private CustomerExternalIdentityRepository identityRepository;

    @Autowired
    private CustomerAccountMergeService mergeService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private CustomerRoleRepository roleRepository;

    @Autowired
    private CustomerAccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from customer_notification");
        jdbcTemplate.update("delete from customer_identity_link_request");
        roleRepository.deleteAll();
        identityRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void googleInbox_enforcesOwnershipDeduplicatesReadsAndPaginatesNewestFirst() throws Exception {
        var owner = accountService.resolveCustomer(google("notification-owner", "owner@example.test"));
        var outsider = accountService.resolveCustomer(google("notification-outsider", "other@example.test"));
        Assertions.assertTrue(recorder.record(
                owner.customerId(),
                new CleaningOrderCustomerNotification.Accepted(41L)
        ));
        Assertions.assertFalse(recorder.record(
                owner.customerId(),
                new CleaningOrderCustomerNotification.Accepted(41L)
        ));
        Assertions.assertTrue(recorder.record(
                owner.customerId(),
                new CleaningOrderCustomerNotification.Cancelled(41L)
        ));
        long newestId = jdbcTemplate.queryForObject(
                "select max(id) from customer_notification where customer_id = ?",
                Long.class,
                owner.customerId()
        );

        mvc.perform(get("/api/v1/account/notifications?page=0&size=1")
                        .with(oidcLogin().oidcUser(oidcUser("notification-owner", "owner@example.test"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content[0].id").value(newestId))
                .andExpect(jsonPath("$.content[0].targetPath").value("/cleaning/orders/41"));
        mvc.perform(get("/api/v1/account/notifications/unread-count")
                        .with(oidcLogin().oidcUser(oidcUser("notification-owner", "owner@example.test"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(2));

        mvc.perform(post("/api/v1/account/notifications/{id}/read", newestId)
                        .with(oidcLogin().oidcUser(oidcUser("notification-outsider", "other@example.test")))
                        .with(csrf()))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/v1/account/notifications/{id}/read", newestId)
                        .with(oidcLogin().oidcUser(oidcUser("notification-owner", "owner@example.test")))
                        .with(csrf()))
                .andExpect(status().isNoContent());
        mvc.perform(post("/api/v1/account/notifications/{id}/read", newestId)
                        .with(oidcLogin().oidcUser(oidcUser("notification-owner", "owner@example.test")))
                        .with(csrf()))
                .andExpect(status().isNoContent());
        mvc.perform(post("/api/v1/account/notifications/read-all")
                        .with(oidcLogin().oidcUser(oidcUser("notification-owner", "owner@example.test")))
                        .with(csrf()))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/account/notifications/unread-count")
                        .with(oidcLogin().oidcUser(oidcUser("notification-owner", "owner@example.test"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(0));

        Assertions.assertNotEquals(owner.customerId(), outsider.customerId());
    }

    @Test
    void linkedGoogleAndTelegramIdentities_readTheSamePersistentInbox() throws Exception {
        var customer = accountService.resolveCustomer(google("linked-google", "linked@example.test"));
        var telegram = accountService.resolveCustomer(new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.TELEGRAM,
                Long.toString(TELEGRAM_ID),
                "linked_customer",
                "Linked Customer",
                "ru",
                null,
                false,
                true
        ));
        transactionTemplate.executeWithoutResult(ignored ->
                mergeService.mergeInto(customer.customerId(), telegram.customerId())
        );
        recorder.record(customer.customerId(), new CleaningOrderCustomerNotification.Accepted(52L));

        mvc.perform(get("/api/v1/account/notifications")
                        .with(oidcLogin().oidcUser(oidcUser("linked-google", "linked@example.test"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(get("/api/v1/account/notifications")
                        .header("Authorization", telegramAuthorization()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].type").value("CLEANING_ORDER_ACCEPTED"));
    }

    private static AuthenticatedCustomerIdentity google(String subject, String email) {
        return new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.GOOGLE,
                subject,
                email,
                "Web Customer",
                "en",
                email,
                true,
                false
        );
    }

    private static OidcUser oidcUser(String subject, String email) {
        Instant issuedAt = Instant.parse("2026-09-03T10:00:00Z");
        OidcIdToken token = OidcIdToken.withTokenValue("notification-id-token")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(3600))
                .subject(subject)
                .claim("name", "Web Customer")
                .claim("email", email)
                .claim("email_verified", true)
                .build();
        return new DefaultOidcUser(java.util.List.of(), token);
    }

    private static String telegramAuthorization() {
        String userJson = """
                {"id":%d,"first_name":"Linked","username":"linked_customer","language_code":"ru","allows_write_to_pm":true}
                """.formatted(TELEGRAM_ID).strip();
        return "tma " + TelegramInitDataTestFactory.signed(BOT_TOKEN, Instant.now(), userJson);
    }
}
