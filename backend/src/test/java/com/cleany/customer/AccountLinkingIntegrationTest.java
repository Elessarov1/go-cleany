package com.cleany.customer;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.web.servlet.MockMvc;

import com.cleany.authorization.CustomerRoleRepository;
import com.cleany.authorization.PlatformRole;
import com.cleany.authorization.PlatformRoleService;
import com.cleany.base.BaseIntegrationTest;
import com.cleany.telegram.TelegramInitDataTestFactory;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AccountLinkingIntegrationTest extends BaseIntegrationTest {

    private static final String BOT_TOKEN = "123456789:test-token";
    private static final long TELEGRAM_ID = 912345678L;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerIdentityLinkRequestRepository requestRepository;

    @Autowired
    private CustomerRoleRepository roleRepository;

    @Autowired
    private PlatformRoleService roleService;

    @Autowired
    private CustomerExternalIdentityRepository identityRepository;

    @Autowired
    private CustomerAccountRepository accountRepository;

    @Autowired
    private CustomerAccountService accountService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        requestRepository.deleteAll();
        roleRepository.deleteAll();
        identityRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void separateGoogleAndTelegramAccountsMergeOnlyAfterExplicitTelegramConfirmation() throws Exception {
        String initiatedJson = mvc.perform(post("/api/v1/account/link/telegram")
                        .with(oidcLogin().oidcUser(oidcUser(
                                "link-google-sub",
                                "linker@example.test",
                                true
                        )))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String token = queryParameter(
                objectMapper.readTree(initiatedJson).path("deepLink").asText(),
                "startapp"
        );
        Assertions.assertNotNull(token);
        Assertions.assertEquals(1L, requestRepository.count());
        Assertions.assertFalse(requestRepository.findAll().getFirst().getTokenHash().contains(token));

        String telegramAuthorization = telegramAuthorization(true);
        mvc.perform(get("/api/v1/account/identities").header("Authorization", telegramAuthorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identities[?(@.provider == 'TELEGRAM')].linked").value(true));

        CustomerExternalIdentity google = identityRepository
                .findByProviderAndExternalSubject(ExternalIdentityProvider.GOOGLE, "link-google-sub")
                .orElseThrow();
        CustomerExternalIdentity telegram = identityRepository
                .findByProviderAndExternalSubject(
                        ExternalIdentityProvider.TELEGRAM,
                        Long.toString(TELEGRAM_ID)
                )
                .orElseThrow();
        long targetCustomerId = google.getCustomerId();
        long sourceCustomerId = telegram.getCustomerId();
        Assertions.assertNotEquals(targetCustomerId, sourceCustomerId);
        accountService.updateNormalizedPhone(sourceCustomerId, "+905551234567");
        roleService.ensureRole(sourceCustomerId, PlatformRole.ADMIN);
        jdbcTemplate.update(
                "update customer_account set created_at = ? where id = ?",
                java.sql.Timestamp.from(Instant.parse("2026-01-01T00:00:00Z")),
                sourceCustomerId
        );

        mvc.perform(post("/api/v1/account/link/telegram/confirm")
                        .header("Authorization", telegramAuthorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identities.length()").value(2));

        CustomerExternalIdentity linkedGoogle = identityRepository
                .findByProviderAndExternalSubject(ExternalIdentityProvider.GOOGLE, "link-google-sub")
                .orElseThrow();
        CustomerExternalIdentity linkedTelegram = identityRepository
                .findByProviderAndExternalSubject(
                        ExternalIdentityProvider.TELEGRAM,
                        Long.toString(TELEGRAM_ID)
                )
                .orElseThrow();
        CustomerAccount target = accountRepository.findById(targetCustomerId).orElseThrow();
        Assertions.assertAll(
                () -> Assertions.assertEquals(targetCustomerId, linkedGoogle.getCustomerId()),
                () -> Assertions.assertEquals(targetCustomerId, linkedTelegram.getCustomerId()),
                () -> Assertions.assertFalse(accountRepository.existsById(sourceCustomerId)),
                () -> Assertions.assertEquals(1L, accountRepository.count()),
                () -> Assertions.assertEquals("+905551234567", target.getPhone()),
                () -> Assertions.assertEquals(Instant.parse("2026-01-01T00:00:00Z"), target.getCreatedAt()),
                () -> Assertions.assertTrue(roleService.hasRole(targetCustomerId, PlatformRole.ADMIN)),
                () -> Assertions.assertTrue(linkedTelegram.isWriteAccessAllowed())
        );

        mvc.perform(post("/api/v1/account/link/telegram/confirm")
                        .header("Authorization", telegramAuthorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("account_link_consumed"));
    }

    @Test
    void invalidTokenAndStandaloneTelegramAdminBootstrapAreRejected() throws Exception {
        String telegramAuthorization = telegramAuthorization(false);
        mvc.perform(post("/api/v1/account/link/telegram/confirm")
                        .header("Authorization", telegramAuthorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"unknown-token\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("account_link_invalid"));

        mvc.perform(get("/api/v1/account/identities").header("Authorization", telegramAuthorization))
                .andExpect(status().isOk());
        CustomerExternalIdentity telegram = identityRepository
                .findByProviderAndExternalSubject(
                        ExternalIdentityProvider.TELEGRAM,
                        Long.toString(TELEGRAM_ID)
                )
                .orElseThrow();
        Assertions.assertFalse(roleService.hasRole(telegram.getCustomerId(), PlatformRole.ADMIN));
    }

    @Test
    void concurrentConfirmationConsumesTokenOnceWithoutPartialMerge() throws Exception {
        String initiatedJson = mvc.perform(post("/api/v1/account/link/telegram")
                        .with(oidcLogin().oidcUser(oidcUser(
                                "concurrent-google-sub",
                                "concurrent@example.test",
                                true
                        )))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String token = queryParameter(
                objectMapper.readTree(initiatedJson).path("deepLink").asText(),
                "startapp"
        );
        String telegramAuthorization = telegramAuthorization(true);
        mvc.perform(get("/api/v1/account/identities").header("Authorization", telegramAuthorization))
                .andExpect(status().isOk());

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var confirmations = List.of(
                    executor.submit(() -> confirmAfterBarrier(token, telegramAuthorization, ready, start)),
                    executor.submit(() -> confirmAfterBarrier(token, telegramAuthorization, ready, start))
            );
            Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();

            List<Integer> statuses = confirmations.stream()
                    .map(future -> {
                        try {
                            return future.get(10, TimeUnit.SECONDS);
                        } catch (Exception exception) {
                            throw new IllegalStateException(exception);
                        }
                    })
                    .sorted()
                    .toList();
            Assertions.assertEquals(List.of(200, 409), statuses);
        }

        CustomerExternalIdentity google = identityRepository
                .findByProviderAndExternalSubject(ExternalIdentityProvider.GOOGLE, "concurrent-google-sub")
                .orElseThrow();
        CustomerExternalIdentity telegram = identityRepository
                .findByProviderAndExternalSubject(
                        ExternalIdentityProvider.TELEGRAM,
                        Long.toString(TELEGRAM_ID)
                )
                .orElseThrow();
        Assertions.assertAll(
                () -> Assertions.assertEquals(google.getCustomerId(), telegram.getCustomerId()),
                () -> Assertions.assertEquals(1L, accountRepository.count()),
                () -> Assertions.assertNotNull(requestRepository.findAll().getFirst().getConsumedAt())
        );
    }

    private int confirmAfterBarrier(
            String token,
            String telegramAuthorization,
            CountDownLatch ready,
            CountDownLatch start
    ) throws Exception {
        ready.countDown();
        if (!start.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Concurrent confirmation start timed out");
        }
        return mvc.perform(post("/api/v1/account/link/telegram/confirm")
                        .header("Authorization", telegramAuthorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\"}"))
                .andReturn()
                .getResponse()
                .getStatus();
    }

    private static String telegramAuthorization(boolean allowsWriteToPm) {
        String userJson = """
                {"id":%d,"first_name":"Alex","username":"alex","language_code":"ru","allows_write_to_pm":%s}
                """.formatted(TELEGRAM_ID, allowsWriteToPm).strip();
        return "tma " + TelegramInitDataTestFactory.signed(BOT_TOKEN, Instant.now(), userJson);
    }

    private static OidcUser oidcUser(String subject, String email, boolean verified) {
        Instant issuedAt = Instant.parse("2026-09-02T10:00:00Z");
        OidcIdToken token = OidcIdToken.withTokenValue("test-id-token")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(3600))
                .subject(subject)
                .claim("name", "Web Customer")
                .claim("email", email)
                .claim("email_verified", verified)
                .build();
        return new DefaultOidcUser(java.util.List.of(), token);
    }

    private static String queryParameter(String uri, String name) {
        String query = URI.create(uri).getQuery();
        if (query == null) return null;
        return Arrays.stream(query.split("&"))
                .map(pair -> pair.split("=", 2))
                .filter(parts -> parts.length == 2 && parts[0].equals(name))
                .map(parts -> parts[1])
                .findFirst()
                .orElse(null);
    }
}
