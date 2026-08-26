package com.cleany.authentication;

import jakarta.servlet.FilterChain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.atomic.AtomicReference;

import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.telegram.TelegramInitDataValidator;
import com.cleany.telegram.TelegramPrincipal;

class TmaAuthenticationFilterTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void tmaAuthorizationHeader_populatesGenericSecurityPrincipal() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "tma signed-init-data");
        var validator = Mockito.mock(TelegramInitDataValidator.class);
        var errorWriter = Mockito.mock(SecurityErrorWriter.class);
        var chain = Mockito.mock(FilterChain.class);
        var requestPrincipal = new AtomicReference<>();
        Mockito.doAnswer(invocation -> {
            requestPrincipal.set(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return null;
        }).when(chain).doFilter(Mockito.any(), Mockito.any());
        Mockito.when(validator.validate("signed-init-data")).thenReturn(
                new TelegramPrincipal(900001L, "alex", "Alex", null, "ru")
        );

        new TmaAuthenticationFilter(validator, errorWriter).doFilter(
                request,
                new MockHttpServletResponse(),
                chain
        );

        Assertions.assertEquals(
                new AuthenticatedCustomerIdentity(
                        ExternalIdentityProvider.TELEGRAM,
                        "900001",
                        "alex",
                        "Alex",
                        "ru"
                ),
                requestPrincipal.get()
        );
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
        Mockito.verify(chain).doFilter(Mockito.any(), Mockito.any());
        Mockito.verifyNoInteractions(errorWriter);
    }

    @Test
    void requestWithoutTmaHeader_isLeftForOtherAuthenticationMechanisms() throws Exception {
        var validator = Mockito.mock(TelegramInitDataValidator.class);
        var errorWriter = Mockito.mock(SecurityErrorWriter.class);
        var chain = Mockito.mock(FilterChain.class);

        new TmaAuthenticationFilter(validator, errorWriter).doFilter(
                new MockHttpServletRequest(),
                new MockHttpServletResponse(),
                chain
        );

        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
        Mockito.verify(chain).doFilter(Mockito.any(), Mockito.any());
        Mockito.verifyNoInteractions(validator, errorWriter);
    }

    @Test
    void malformedTmaCredential_isRejectedBeforeController() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "tma invalid");
        var response = new MockHttpServletResponse();
        var validator = Mockito.mock(TelegramInitDataValidator.class);
        var errorWriter = Mockito.mock(SecurityErrorWriter.class);
        var chain = Mockito.mock(FilterChain.class);
        Mockito.when(validator.validate("invalid"))
                .thenThrow(new CustomerAuthenticationRequiredException());

        new TmaAuthenticationFilter(validator, errorWriter).doFilter(request, response, chain);

        Mockito.verify(errorWriter).write(
                response,
                401,
                "authentication_required",
                "Valid Telegram Mini App authentication is required"
        );
        Mockito.verifyNoInteractions(chain);
    }
}
