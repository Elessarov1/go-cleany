package com.cleany.authentication;

import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Base64;
import com.jayway.jsonpath.JsonPath;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;

import com.cleany.authorization.CustomerRoleRepository;
import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountStatus;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderService;
import com.cleany.order.CleaningOrderStatus;
import com.cleany.referral.ReferralEligibilityService;
import com.cleany.referral.ReferralService;
import com.cleany.telegram.bot.TelegramUpdate;
import com.cleany.telegram.TelegramInitDataTestFactory;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class WebSecurityIntegrationTest extends BaseIntegrationTest {

    @Test
    void nativeBootstrapAndRefreshDoNotRequireBrowserCsrf() throws Exception {
        mvc.perform(post("/api/v1/auth/native/challenges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"provider\":\"GOOGLE\",\"clientType\":\"ANDROID\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("native_auth_unavailable"));

        mvc.perform(post("/api/v1/auth/sessions/refresh")
                        .header("Idempotency-Key", "refresh-attempt-one")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"lp_rt_invalid-token-value\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("native_auth_unavailable"));
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CleaningOrderRepository orderRepository;

    @Autowired
    private CleaningOrderService orderService;

    @Autowired
    private CustomerRoleRepository roleRepository;

    @Autowired
    private CustomerExternalIdentityRepository identityRepository;

    @Autowired
    private CustomerAccountRepository accountRepository;

    @Autowired
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    private TelegramNativeLoginService telegramNativeLoginService;

    @Autowired
    private ReferralService referralService;

    @Autowired
    private ReferralEligibilityService referralEligibilityService;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from notification_delivery");
        jdbcTemplate.update("delete from communication_endpoint");
        jdbcTemplate.update("delete from customer_notification_preference");
        jdbcTemplate.update("delete from authentication_challenge");
        jdbcTemplate.update("delete from used_refresh_token");
        jdbcTemplate.update("delete from customer_session");
        jdbcTemplate.update("delete from idempotency_record");
        jdbcTemplate.update("delete from apple_identity_credential");
        jdbcTemplate.update("delete from external_provider_job");
        jdbcTemplate.update("delete from security_audit_event");
        jdbcTemplate.update("delete from spring_session_attributes");
        jdbcTemplate.update("delete from spring_session");
        jdbcTemplate.update("delete from rental_search_event");
        jdbcTemplate.update("update rental_booking set search_execution_id = null");
        jdbcTemplate.update("delete from rental_search_execution");
        jdbcTemplate.update("delete from customer_reminder");
        jdbcTemplate.update("delete from customer_acquisition");
        jdbcTemplate.update("delete from partner_payout");
        jdbcTemplate.update("update cleaning_order set applied_reward_id = null");
        jdbcTemplate.update("delete from referral_reward");
        orderRepository.deleteAll();
        jdbcTemplate.update("delete from referral_code");
        jdbcTemplate.update("delete from referral_partner");
        jdbcTemplate.update("delete from referral_eligibility_marker");
        roleRepository.deleteAll();
        identityRepository.deleteAll();
        accountRepository.deleteAll();
        jdbcTemplate.update("update platform_service_state set status = 'ENABLED'");
    }

    @Test
    void freshGoogleReauthenticationLinksTelegramThroughOneTimeBotHandoff() throws Exception {
        OidcUser google = freshUser("google-link-owner", "link-owner@example.test", true);
        String created = mvc.perform(post("/api/v1/account/identity-links")
                        .with(oidcLogin().oidcUser(google))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"provider\":\"TELEGRAM\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value("TELEGRAM"))
                .andExpect(jsonPath("$.telegramDeepLink").value(containsString("?start=link_")))
                .andReturn().getResponse().getContentAsString();

        String attemptId = JsonPath.read(created, "$.id");
        String deepLink = JsonPath.read(created, "$.telegramDeepLink");
        String startParameter = URI.create(deepLink).getRawQuery().substring("start=".length());
        org.assertj.core.api.Assertions.assertThat(telegramNativeLoginService.approve(
                startParameter,
                new TelegramUpdate.TelegramUser(900001L, "linked_user", "Linked", "User", "ru")
        )).isTrue();

        mvc.perform(post("/api/v1/account/identity-links/{id}/confirm", attemptId)
                        .with(oidcLogin().oidcUser(freshUser(
                                "google-link-owner", "link-owner@example.test", true)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identities.length()").value(2))
                .andExpect(jsonPath("$.identities[1].provider").value("TELEGRAM"))
                .andExpect(jsonPath("$.identities[1].username").value("linked_user"));

        org.assertj.core.api.Assertions.assertThat(accountRepository.count()).isEqualTo(1);
    }

    @Test
    void emptyCustomerAccountDeletionLeavesOnlyAnAnonymizedTombstone() throws Exception {
        String subject = "google-delete-owner";
        String created = mvc.perform(post("/api/v1/account/deletion-requests")
                        .with(oidcLogin().oidcUser(freshUser(subject, "delete-owner@example.test", true)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String requestId = JsonPath.read(created, "$.id");

        mvc.perform(post("/api/v1/account/deletion-requests/{id}/confirm", requestId)
                        .with(oidcLogin().oidcUser(freshUser(subject, "delete-owner@example.test", true)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"challengeId\":\"" + requestId + "\"}"))
                .andExpect(status().isNoContent());

        var tombstone = accountRepository.findAll().getFirst();
        org.assertj.core.api.Assertions.assertThat(tombstone.getStatus())
                .isEqualTo(CustomerAccountStatus.DELETED);
        org.assertj.core.api.Assertions.assertThat(tombstone.getPhone()).isNull();
        org.assertj.core.api.Assertions.assertThat(tombstone.getDeletedAt()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(identityRepository.count()).isZero();
        org.assertj.core.api.Assertions.assertThat(jdbcTemplate.queryForObject(
                "select count(*) from referral_eligibility_marker", Long.class)).isZero();
    }

    @Test
    void telegramMiniAppDeletionUsesFreshInitDataProof() throws Exception {
        long telegramId = 88119900L;
        String initData = TelegramInitDataTestFactory.signed(
                "123456789:test-token", Instant.now(), telegramUser(telegramId));
        String created = mvc.perform(post("/api/v1/account/deletion-requests")
                        .header("Authorization", "tma " + initData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value("TELEGRAM"))
                .andReturn().getResponse().getContentAsString();
        String requestId = JsonPath.read(created, "$.id");

        mvc.perform(post("/api/v1/account/deletion-requests/{id}/confirm", requestId)
                        .header("Authorization", "tma " + initData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"challengeId":"%s","telegramInitData":"%s"}
                                """.formatted(requestId, initData)))
                .andExpect(status().isNoContent());

        org.assertj.core.api.Assertions.assertThat(accountRepository.findAll()).singleElement()
                .extracting(account -> account.getStatus()).isEqualTo(CustomerAccountStatus.DELETED);
        org.assertj.core.api.Assertions.assertThat(identityRepository.count()).isZero();
    }

    @Test
    void administratorCannotCreateSelfDeletionRequest() throws Exception {
        mvc.perform(post("/api/v1/account/deletion-requests")
                        .with(oidcLogin().oidcUser(freshUser(
                                "google-admin-delete", "admin@example.test", true)))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("admin_account_self_deletion_forbidden"));
    }

    @Test
    void completedCustomerDeletionRetainsOnlyProviderMarkersAndBlocksReferralReuse() throws Exception {
        String subject = "google-referral-deletion";
        String email = "referral-deletion@example.test";
        OidcUser user = freshUser(subject, email, true);
        String orderJson = mvc.perform(post("/api/v1/cleaning/orders")
                        .with(oidcLogin().oidcUser(user))
                        .with(csrf())
                        .header("Idempotency-Key", "referral-deletion-completed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cleaningOrderBody()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long orderId = ((Number) JsonPath.read(orderJson, "$.id")).longValue();
        long customerId = orderRepository.findById(orderId).orElseThrow().getCustomerId();
        completeCleaning(orderId);
        String customerCode = jdbcTemplate.queryForObject(
                "select code from referral_code where customer_id = ? and active = true",
                String.class, customerId);
        long otherReferrerId = accountRepository.save(new com.cleany.customer.CustomerAccount(Instant.now()))
                .getId();
        jdbcTemplate.update("""
                insert into referral_code (
                    code, owner_type, customer_id, active, created_at
                ) values ('OTHER10', 'CUSTOMER', ?, true, ?)
                """, otherReferrerId, Timestamp.from(Instant.now()));
        var partner = referralService.createPartner("Deletion guard partner");

        long linkedTelegramSubject = 88110022L;
        jdbcTemplate.update("""
                insert into customer_external_identity (
                    customer_id, provider, issuer, external_subject, username, display_name,
                    language_code, email_verified, write_access_allowed, last_seen_at
                ) values (?, 'TELEGRAM', 'https://telegram.org', ?, ?, 'Linked customer',
                          'ru', false, false, ?)
                """, customerId, Long.toString(linkedTelegramSubject), "linked_customer",
                Timestamp.from(Instant.now()));
        jdbcTemplate.update("""
                insert into referral_reward (customer_id, source_order_id, status, created_at)
                values (?, ?, 'AVAILABLE', ?)
                """, customerId, orderId, Timestamp.from(Instant.now()));

        deleteGoogleAccount(subject, email);

        org.assertj.core.api.Assertions.assertThat(jdbcTemplate.queryForObject(
                "select count(*) from referral_eligibility_marker", Long.class)).isEqualTo(2L);
        org.assertj.core.api.Assertions.assertThat(jdbcTemplate.queryForObject(
                "select extract(epoch from (expires_at - created_at))::bigint "
                        + "from referral_eligibility_marker limit 1", Long.class))
                .isEqualTo(365L * 24 * 60 * 60);
        org.assertj.core.api.Assertions.assertThat(jdbcTemplate.queryForList(
                "select identity_digest from referral_eligibility_marker", String.class))
                .allSatisfy(digest -> {
                    org.assertj.core.api.Assertions.assertThat(digest).hasSize(64);
                    org.assertj.core.api.Assertions.assertThat(digest).doesNotContain(subject);
                    org.assertj.core.api.Assertions.assertThat(digest)
                            .doesNotContain(Long.toString(linkedTelegramSubject));
                });
        org.assertj.core.api.Assertions.assertThat(jdbcTemplate.queryForObject(
                "select active from referral_code where code = ?", Boolean.class, customerCode)).isFalse();
        org.assertj.core.api.Assertions.assertThat(jdbcTemplate.queryForObject(
                "select status from referral_reward where source_order_id = ?", String.class, orderId))
                .isEqualTo("REVOKED");
        org.assertj.core.api.Assertions.assertThat(jdbcTemplate.queryForObject(
                "select revoked_at is not null and reserved_order_id is null "
                        + "from referral_reward where source_order_id = ?", Boolean.class, orderId)).isTrue();

        mvc.perform(post("/api/v1/cleaning/orders/quote")
                        .with(oidcLogin().oidcUser(freshUser(subject, email, true)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cleaningQuoteBody(partner.referralCode())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("referral_not_applicable"));
        mvc.perform(post("/api/v1/cleaning/orders/quote")
                        .with(oidcLogin().oidcUser(freshUser(subject, email, true)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cleaningQuoteBody("OTHER10")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("referral_not_applicable"));
        mvc.perform(post("/api/v1/cleaning/orders")
                        .with(oidcLogin().oidcUser(freshUser(subject, email, true)))
                        .with(csrf())
                        .header("Idempotency-Key", "same-google-after-deletion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cleaningOrderBody(partner.referralCode())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("referral_not_applicable"));

        String linkedTelegram = TelegramInitDataTestFactory.signed(
                "123456789:test-token", Instant.now(), telegramUser(linkedTelegramSubject));
        mvc.perform(post("/api/v1/cleaning/orders")
                        .header("Authorization", "tma " + linkedTelegram)
                        .header("Idempotency-Key", "same-telegram-after-deletion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cleaningOrderBody(partner.referralCode())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("referral_not_applicable"));

        String newTelegram = TelegramInitDataTestFactory.signed(
                "123456789:test-token", Instant.now(), telegramUser(linkedTelegramSubject + 1));
        mvc.perform(post("/api/v1/cleaning/orders")
                        .header("Authorization", "tma " + newTelegram)
                        .header("Idempotency-Key", "new-telegram-after-deletion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cleaningOrderBody(partner.referralCode())))
                .andExpect(status().isCreated());

        jdbcTemplate.update("update referral_eligibility_marker set created_at = ?, expires_at = ?",
                Timestamp.from(Instant.now().minusSeconds(2)),
                Timestamp.from(Instant.now().minusSeconds(1)));
        org.assertj.core.api.Assertions.assertThat(
                referralEligibilityService.deleteExpiredMarkers(Instant.now(), 100)).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(jdbcTemplate.queryForObject(
                "select count(*) from referral_eligibility_marker", Long.class)).isZero();
    }

    @Test
    void blockedDeletionRollsBackReferralCleanupAndMarkers() throws Exception {
        String subject = "google-referral-deletion-blocked";
        String email = "referral-deletion-blocked@example.test";
        OidcUser user = freshUser(subject, email, true);
        String completedJson = mvc.perform(post("/api/v1/cleaning/orders")
                        .with(oidcLogin().oidcUser(user)).with(csrf())
                        .header("Idempotency-Key", "deletion-rollback-completed")
                        .contentType(MediaType.APPLICATION_JSON).content(cleaningOrderBody()))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long completedId = ((Number) JsonPath.read(completedJson, "$.id")).longValue();
        long customerId = orderRepository.findById(completedId).orElseThrow().getCustomerId();
        completeCleaning(completedId);
        String code = jdbcTemplate.queryForObject(
                "select code from referral_code where customer_id = ? and active = true",
                String.class, customerId);
        jdbcTemplate.update("""
                insert into referral_reward (customer_id, source_order_id, status, created_at)
                values (?, ?, 'AVAILABLE', ?)
                """, customerId, completedId, Timestamp.from(Instant.now()));

        String activeJson = mvc.perform(post("/api/v1/cleaning/orders")
                        .with(oidcLogin().oidcUser(freshUser(subject, email, true))).with(csrf())
                        .header("Idempotency-Key", "deletion-rollback-active")
                        .contentType(MediaType.APPLICATION_JSON).content(cleaningOrderBody()))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long activeId = ((Number) JsonPath.read(activeJson, "$.id")).longValue();
        orderService.acceptOrder(activeId, 123456789L);

        String request = mvc.perform(post("/api/v1/account/deletion-requests")
                        .with(oidcLogin().oidcUser(freshUser(subject, email, true))).with(csrf()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String requestId = JsonPath.read(request, "$.id");
        mvc.perform(post("/api/v1/account/deletion-requests/{id}/confirm", requestId)
                        .with(oidcLogin().oidcUser(freshUser(subject, email, true))).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"challengeId\":\"" + requestId + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("account_deletion_blocked_by_active_operation"));

        org.assertj.core.api.Assertions.assertThat(jdbcTemplate.queryForObject(
                "select count(*) from referral_eligibility_marker", Long.class)).isZero();
        org.assertj.core.api.Assertions.assertThat(jdbcTemplate.queryForObject(
                "select active from referral_code where code = ?", Boolean.class, code)).isTrue();
        org.assertj.core.api.Assertions.assertThat(jdbcTemplate.queryForObject(
                "select status from referral_reward where source_order_id = ?", String.class, completedId))
                .isEqualTo("RESERVED");
        org.assertj.core.api.Assertions.assertThat(jdbcTemplate.queryForObject(
                "select reserved_order_id from referral_reward where source_order_id = ?",
                Long.class, completedId)).isEqualTo(activeId);
        org.assertj.core.api.Assertions.assertThat(orderRepository.findById(activeId).orElseThrow().getStatus())
                .isEqualTo(CleaningOrderStatus.ACCEPTED);
    }

    @Test
    void accountDeletionCancelsCancellableWorkAndAnonymizesTheRetainedOperation() throws Exception {
        String subject = "google-delete-with-order";
        OidcUser customer = freshUser(subject, "delete-with-order@example.test", true);
        mvc.perform(post("/api/v1/cleaning/orders")
                        .with(oidcLogin().oidcUser(customer))
                        .with(csrf())
                        .header("Idempotency-Key", "delete-with-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cleaningOrderBody()))
                .andExpect(status().isCreated());

        String created = mvc.perform(post("/api/v1/account/deletion-requests")
                        .with(oidcLogin().oidcUser(freshUser(
                                subject, "delete-with-order@example.test", true)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String requestId = JsonPath.read(created, "$.id");

        mvc.perform(post("/api/v1/account/deletion-requests/{id}/confirm", requestId)
                        .with(oidcLogin().oidcUser(freshUser(
                                subject, "delete-with-order@example.test", true)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"challengeId\":\"" + requestId + "\"}"))
                .andExpect(status().isNoContent());

        var retainedOrder = orderRepository.findAll().getFirst();
        org.assertj.core.api.Assertions.assertThat(retainedOrder.getStatus())
                .isEqualTo(CleaningOrderStatus.CANCELLED);
        org.assertj.core.api.Assertions.assertThat(jdbcTemplate.queryForObject(
                "select customer_name from cleaning_order where id = ?", String.class, retainedOrder.getId()
        )).isEqualTo("Deleted customer");
        org.assertj.core.api.Assertions.assertThat(jdbcTemplate.queryForObject(
                "select address from cleaning_order where id = ?", String.class, retainedOrder.getId()
        )).isEqualTo("deleted");
        org.assertj.core.api.Assertions.assertThat(accountRepository.findAll().getFirst().getStatus())
                .isEqualTo(CustomerAccountStatus.DELETED);
    }

    @Test
    void nonCancellableWorkRollsBackAccountDeletionWithoutPartialChanges() throws Exception {
        String subject = "google-delete-blocked";
        OidcUser customer = freshUser(subject, "delete-blocked@example.test", true);
        String orderJson = mvc.perform(post("/api/v1/cleaning/orders")
                        .with(oidcLogin().oidcUser(customer))
                        .with(csrf())
                        .header("Idempotency-Key", "delete-blocked-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cleaningOrderBody()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Number orderId = JsonPath.read(orderJson, "$.id");
        orderService.acceptOrder(orderId.longValue(), 123456789L);

        String created = mvc.perform(post("/api/v1/account/deletion-requests")
                        .with(oidcLogin().oidcUser(freshUser(
                                subject, "delete-blocked@example.test", true)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String requestId = JsonPath.read(created, "$.id");

        mvc.perform(post("/api/v1/account/deletion-requests/{id}/confirm", requestId)
                        .with(oidcLogin().oidcUser(freshUser(
                                subject, "delete-blocked@example.test", true)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"challengeId\":\"" + requestId + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("account_deletion_blocked_by_active_operation"));

        org.assertj.core.api.Assertions.assertThat(orderRepository.findById(orderId.longValue()).orElseThrow()
                .getStatus()).isEqualTo(CleaningOrderStatus.ACCEPTED);
        org.assertj.core.api.Assertions.assertThat(accountRepository.findAll().getFirst().getStatus())
                .isEqualTo(CustomerAccountStatus.ACTIVE);
        org.assertj.core.api.Assertions.assertThat(identityRepository.count()).isEqualTo(1);
    }

    @Test
    void anonymousAndOidcCurrentUserResponsesExposeNoProviderTokens() throws Exception {
        mvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.loginProviders.google.available").value(false));

        mvc.perform(get("/api/v1/auth/me").with(oidcLogin().oidcUser(user(
                        "google-web-1",
                        "customer@example.test",
                        true
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.customerId").isNumber())
                .andExpect(jsonPath("$.displayName").value("Web Customer"))
                .andExpect(jsonPath("$.provider").value("GOOGLE"))
                .andExpect(jsonPath("$.roles").isEmpty())
                .andExpect(jsonPath("$.idToken").doesNotExist())
                .andExpect(jsonPath("$.accessToken").doesNotExist());
    }

    @Test
    void anonymousCatalogShowsEnabledAndHidesInTestServices() throws Exception {
        mvc.perform(get("/api/v1/catalog/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        jdbcTemplate.update(
                "update platform_service_state set status = 'IN_TEST' "
                        + "where service = 'CLEANING'"
        );
        clearPlatformServiceStateCache();

        mvc.perform(get("/api/v1/catalog/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].service").value("RENTAL"))
                .andExpect(jsonPath("$[1].service").value("TRANSFER"));
    }

    @Test
    void anonymousRentalSearchCreatesNoAccountAndInTestAllowsOnlyPersistedAdmin() throws Exception {
        long accountsBefore = accountRepository.count();
        mvc.perform(get("/api/v1/rental/search"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, containsString("no-store")))
                .andExpect(jsonPath("$.searchExecutionId").isString())
                .andExpect(jsonPath("$.criteria.mode").value("BROWSE_ALL"))
                .andExpect(jsonPath("$.hasMore").value(false))
                .andExpect(jsonPath("$.nextCursor").value(org.hamcrest.Matchers.nullValue()));
        org.assertj.core.api.Assertions.assertThat(accountRepository.count()).isEqualTo(accountsBefore);

        mvc.perform(get("/api/v1/rental/search").param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("invalid_cursor"));
        mvc.perform(get("/api/v1/rental/search").param("cursor", "damaged"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("invalid_cursor"));

        jdbcTemplate.update(
                "update platform_service_state set status = 'IN_TEST' where service = 'RENTAL'"
        );
        clearPlatformServiceStateCache();
        mvc.perform(get("/api/v1/rental/search"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("service_not_available"));

        mvc.perform(get("/api/v1/rental/search").with(oidcLogin().oidcUser(user(
                        "rental-search-admin",
                        "admin@example.test",
                        true
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.criteria.mode").value("BROWSE_ALL"));
    }

    @Test
    void legacyRentalCatalogAndAuthenticatedQuoteRoutesAreNotMapped() throws Exception {
        mvc.perform(get("/api/v1/rental/properties"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("resource_not_found"));

        mvc.perform(post("/api/v1/rental/bookings/quote")
                        .with(oidcLogin().oidcUser(user(
                                "legacy-rental-route",
                                "customer@example.test",
                                true
                        )))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("method_not_allowed"));
    }

    @Test
    void disabledGoogleIsReportedAndUnavailableRoutesNeverBecomeInternalError() throws Exception {
        mvc.perform(get("/api/v1/auth/google/login").param("returnTo", "/rent/bookings"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("login_provider_unavailable"));

        mvc.perform(get("/api/v1/auth/google/admin"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("login_provider_unavailable"));

        mvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("resource_not_found"));
    }

    @Test
    void productionDefaultsUseSecureJdbcSessions() {
        org.assertj.core.api.Assertions.assertThat(environment.getProperty(
                "server.servlet.session.cookie.secure",
                Boolean.class
        )).isTrue();
        org.assertj.core.api.Assertions.assertThat(environment.getProperty(
                "spring.session.jdbc.initialize-schema"
        )).isEqualTo("never");
    }

    @Test
    void verifiedGoogleAdminReceivesRoleWhileNonAdminCannotUseAdminApi() throws Exception {
        mvc.perform(get("/api/v1/auth/me").with(oidcLogin().oidcUser(user(
                        "google-admin",
                        "admin@example.test",
                        true
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"));

        mvc.perform(get("/api/v1/admin/platform/services").with(oidcLogin().oidcUser(user(
                        "google-customer",
                        "customer@example.test",
                        true
                ))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("admin_not_authorized"));
    }

    @Test
    void webWriteRequiresCsrfAndSucceedsWithSpringCsrfToken() throws Exception {
        String body = cleaningOrderBody();
        var login = oidcLogin().oidcUser(user(
                "google-web-write",
                "writer@example.test",
                true
        ));

        mvc.perform(post("/api/v1/cleaning/orders")
                        .with(login)
                        .header("Idempotency-Key", "web-csrf-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("access_denied"));

        mvc.perform(post("/api/v1/cleaning/orders")
                        .with(oidcLogin().oidcUser(user(
                                "google-web-write",
                                "writer@example.test",
                                true
                        )))
                        .with(csrf())
                        .header("Idempotency-Key", "web-csrf-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void createOrderIsIdempotentAndRejectsPayloadChangeForTheSameKey() throws Exception {
        String key = "lost-response-order-key";
        String body = cleaningOrderBody();
        String first = mvc.perform(post("/api/v1/cleaning/orders")
                        .with(oidcLogin().oidcUser(user("idempotent-google", "idempotent@example.test", true)))
                        .with(csrf()).header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Number firstId = JsonPath.read(first, "$.id");

        mvc.perform(post("/api/v1/cleaning/orders")
                        .with(oidcLogin().oidcUser(user("idempotent-google", "idempotent@example.test", true)))
                        .with(csrf()).header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(firstId.longValue()));
        mvc.perform(post("/api/v1/cleaning/orders")
                        .with(oidcLogin().oidcUser(user("idempotent-google", "idempotent@example.test", true)))
                        .with(csrf()).header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.replace("Barbaros Cd. 24", "Ataturk Cd. 10")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("idempotency_key_payload_mismatch"));

        org.assertj.core.api.Assertions.assertThat(orderRepository.count()).isEqualTo(1);
    }

    @Test
    void jdbcSessionAuthenticatesSubsequentRequestAndLogoutInvalidatesIt() throws Exception {
        OidcUser oidcUser = user("google-session", "session@example.test", true);
        var authentication = new OAuth2AuthenticationToken(
                oidcUser,
                oidcUser.getAuthorities(),
                "google"
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        Session session = sessionRepository.createSession();
        session.setAttribute(
                "SPRING_SECURITY_CONTEXT",
                context
        );
        save(session);
        Cookie cookie = new Cookie(
                "SESSION",
                Base64.getEncoder().encodeToString(session.getId().getBytes(StandardCharsets.UTF_8))
        );

        mvc.perform(get("/api/v1/auth/me").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.provider").value("GOOGLE"));

        var logout = mvc.perform(delete("/api/v1/auth/sessions/current").cookie(cookie).with(csrf()))
                .andExpect(status().isNoContent())
                .andReturn();
        org.assertj.core.api.Assertions.assertThat(logout.getResponse().getHeaders(HttpHeaders.SET_COOKIE))
                .anyMatch(value -> value.startsWith("SESSION=;") && value.contains("Max-Age=0"))
                .anyMatch(value -> value.startsWith("XSRF-TOKEN=;") && value.contains("Max-Age=0"));

        mvc.perform(get("/api/v1/auth/me").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void save(Session session) {
        ((FindByIndexNameSessionRepository) sessionRepository).save(session);
    }

    private static OidcUser user(String subject, String email, boolean verified) {
        var issuedAt = java.time.Instant.parse("2026-08-25T10:00:00Z");
        var token = OidcIdToken.withTokenValue("test-id-token")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(3600))
                .subject(subject)
                .claim("name", "Web Customer")
                .claim("email", email)
                .claim("email_verified", verified)
                .build();
        return new DefaultOidcUser(java.util.Collections.emptyList(), token);
    }

    private static OidcUser freshUser(String subject, String email, boolean verified) {
        var issuedAt = java.time.Instant.now();
        var token = OidcIdToken.withTokenValue("fresh-test-id-token")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(3600))
                .subject(subject)
                .claim("name", "Web Customer")
                .claim("email", email)
                .claim("email_verified", verified)
                .build();
        return new DefaultOidcUser(java.util.Collections.emptyList(), token);
    }

    private static String cleaningOrderBody() {
        return cleaningOrderBody(null);
    }

    private static String cleaningOrderBody(String referralCode) {
        LocalDate requestedDate = LocalDate.now(ZoneId.of("Europe/Istanbul")).plusDays(1);
        return """
                {
                  "area": "MAHMUTLAR",
                  "address": "Barbaros Cd. 24",
                  "apartmentType": "TWO_PLUS_ONE",
                  "duplex": false,
                  "cleaningType": "REGULAR",
                  "requestedDate": "%s",
                  "phone": "+90 555 123 45 67",
                  "comment": null,
                  "referralCode": %s
                }
                """.formatted(
                requestedDate,
                referralCode == null ? "null" : "\"" + referralCode + "\""
        );
    }

    private static String cleaningQuoteBody(String referralCode) {
        return """
                {
                  "apartmentType": "TWO_PLUS_ONE",
                  "duplex": false,
                  "cleaningType": "REGULAR",
                  "referralCode": "%s"
                }
                """.formatted(referralCode);
    }

    private static String telegramUser(long id) {
        return """
                {"id":%d,"first_name":"Deleted","last_name":"Customer","username":"customer%d"}
                """.formatted(id, id).strip();
    }

    private void completeCleaning(long orderId) {
        orderService.acceptOrder(orderId, 123456789L);
        orderService.markAwaitingReport(orderId, 123456789L);
        orderService.completeOrder(orderId, 123456789L, null);
    }

    private void deleteGoogleAccount(String subject, String email) throws Exception {
        String created = mvc.perform(post("/api/v1/account/deletion-requests")
                        .with(oidcLogin().oidcUser(freshUser(subject, email, true)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String requestId = JsonPath.read(created, "$.id");
        mvc.perform(post("/api/v1/account/deletion-requests/{id}/confirm", requestId)
                        .with(oidcLogin().oidcUser(freshUser(subject, email, true)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"challengeId\":\"" + requestId + "\"}"))
                .andExpect(status().isNoContent());
    }
}
