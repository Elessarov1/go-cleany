package com.cleany.authentication;

import java.time.LocalDate;
import java.time.ZoneId;
import java.nio.charset.StandardCharsets;
import java.net.URI;
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
import com.cleany.telegram.bot.TelegramUpdate;

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
        orderRepository.deleteAll();
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
                .andExpect(jsonPath("$.criteria.mode").value("BROWSE_ALL"));
        org.assertj.core.api.Assertions.assertThat(accountRepository.count()).isEqualTo(accountsBefore);

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
        return new DefaultOidcUser(java.util.List.of(), token);
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
        return new DefaultOidcUser(java.util.List.of(), token);
    }

    private static String cleaningOrderBody() {
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
                  "comment": null
                }
                """.formatted(requestedDate);
    }
}
