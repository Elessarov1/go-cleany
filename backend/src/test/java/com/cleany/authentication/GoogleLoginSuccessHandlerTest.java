package com.cleany.authentication;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class GoogleLoginSuccessHandlerTest {

    private final GoogleLoginSuccessHandler handler = new GoogleLoginSuccessHandler();

    @Test
    void adminLoginReturnsToAdminAndConsumesTarget() throws Exception {
        var request = new MockHttpServletRequest();
        request.getSession().setAttribute(
                GoogleLoginSuccessHandler.SUCCESS_TARGET_SESSION_ATTRIBUTE,
                "/admin"
        );
        var response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, null);

        assertThat(response.getRedirectedUrl()).isEqualTo("/admin");
        assertThat(request.getSession().getAttribute(
                GoogleLoginSuccessHandler.SUCCESS_TARGET_SESSION_ATTRIBUTE
        )).isNull();
    }

    @Test
    void ordinaryAndUnexpectedTargetsReturnToPlatformRoot() throws Exception {
        var ordinaryRequest = new MockHttpServletRequest();
        var ordinaryResponse = new MockHttpServletResponse();
        handler.onAuthenticationSuccess(ordinaryRequest, ordinaryResponse, null);

        var unsafeRequest = new MockHttpServletRequest();
        unsafeRequest.getSession().setAttribute(
                GoogleLoginSuccessHandler.SUCCESS_TARGET_SESSION_ATTRIBUTE,
                "https://attacker.example"
        );
        var unsafeResponse = new MockHttpServletResponse();
        handler.onAuthenticationSuccess(unsafeRequest, unsafeResponse, null);

        assertThat(ordinaryResponse.getRedirectedUrl()).isEqualTo("/");
        assertThat(unsafeResponse.getRedirectedUrl()).isEqualTo("/");
    }
}
