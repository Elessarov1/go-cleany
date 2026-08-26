package com.cleany.authentication;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import com.cleany.base.BaseIntegrationTest;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "web-auth.google.enabled=true",
        "web-auth.google.client-id=test-client-id",
        "web-auth.google.client-secret=test-client-secret"
})
class GoogleOAuthConfigurationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Test
    void configuredProviderIsReportedAndAuthorizationInitiationExists() throws Exception {
        mvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginProviders.google.available").value(true));

        mvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", startsWith(
                        "https://accounts.google.com/o/oauth2/v2/auth"
                )));
    }

    @Test
    void customerAndAdminTargetsAreStoredAcrossAuthorizationRoundTrip() throws Exception {
        assertStoredTarget("/rent/bookings/42?from=notification");

        var adminResult = mvc.perform(get("/api/v1/auth/google/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorization/google"))
                .andReturn();
        org.assertj.core.api.Assertions.assertThat(storedTarget(adminResult)).isEqualTo("/admin");
    }

    @Test
    void externalContinuationTargetIsReplacedWithPlatformRoot() throws Exception {
        assertStoredTarget("https://attacker.example/steal", "/");
    }

    private void assertStoredTarget(String requestedTarget) throws Exception {
        assertStoredTarget(requestedTarget, requestedTarget);
    }

    private void assertStoredTarget(String requestedTarget, String expectedTarget) throws Exception {
        var result = mvc.perform(get("/api/v1/auth/google/login")
                        .param("returnTo", requestedTarget))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorization/google"))
                .andReturn();
        org.assertj.core.api.Assertions.assertThat(storedTarget(result)).isEqualTo(expectedTarget);
    }

    private Object storedTarget(org.springframework.test.web.servlet.MvcResult result) {
        var sessionCookie = result.getResponse().getCookie("SESSION");
        org.assertj.core.api.Assertions.assertThat(sessionCookie).isNotNull();
        var sessionId = new String(
                Base64.getDecoder().decode(sessionCookie.getValue()),
                StandardCharsets.UTF_8
        );
        Session session = sessionRepository.findById(sessionId);
        org.assertj.core.api.Assertions.assertThat(session).isNotNull();
        return session.getAttribute(GoogleLoginSuccessHandler.SUCCESS_TARGET_SESSION_ATTRIBUTE);
    }
}
