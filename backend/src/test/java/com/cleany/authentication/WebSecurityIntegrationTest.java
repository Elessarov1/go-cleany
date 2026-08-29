package com.cleany.authentication;

import java.time.LocalDate;
import java.time.ZoneId;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

import com.cleany.authorization.CustomerRoleRepository;
import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.order.CleaningOrderRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class WebSecurityIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CleaningOrderRepository orderRepository;

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

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from spring_session_attributes");
        jdbcTemplate.update("delete from spring_session");
        orderRepository.deleteAll();
        roleRepository.deleteAll();
        identityRepository.deleteAll();
        accountRepository.deleteAll();
        jdbcTemplate.update("update platform_service_state set status = 'ENABLED'");
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
                .andExpect(jsonPath("$.length()").value(2));

        jdbcTemplate.update(
                "update platform_service_state set status = 'IN_TEST' "
                        + "where service = 'CLEANING'"
        );
        clearPlatformServiceStateCache();

        mvc.perform(get("/api/v1/catalog/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].service").value("RENTAL"));
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
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

        mvc.perform(post("/api/v1/auth/logout").cookie(cookie).with(csrf()))
                .andExpect(status().isNoContent());

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
