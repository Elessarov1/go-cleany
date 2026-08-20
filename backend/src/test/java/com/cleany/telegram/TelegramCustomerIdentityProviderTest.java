package com.cleany.telegram;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

class TelegramCustomerIdentityProviderTest {

    @Test
    void tmaAuthorizationHeader_initDataValidated() {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "tma signed-init-data");
        var validator = Mockito.mock(TelegramInitDataValidator.class);
        var expected = new TelegramPrincipal(900001L, "alex", "Alex", null, "ru");
        Mockito.when(validator.validate("signed-init-data")).thenReturn(expected);

        var provider = new TelegramCustomerIdentityProvider(request, validator);

        Assertions.assertSame(expected, provider.currentCustomer());
    }

    @Test
    void missingAuthorizationHeader_authenticationRejected() {
        var provider = new TelegramCustomerIdentityProvider(
                new MockHttpServletRequest(),
                Mockito.mock(TelegramInitDataValidator.class)
        );

        Assertions.assertThrows(
                CustomerAuthenticationRequiredException.class,
                provider::currentCustomer
        );
    }

    @Test
    void differentAuthorizationScheme_authenticationRejected() {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer signed-init-data");
        var provider = new TelegramCustomerIdentityProvider(
                request,
                Mockito.mock(TelegramInitDataValidator.class)
        );

        Assertions.assertThrows(
                CustomerAuthenticationRequiredException.class,
                provider::currentCustomer
        );
    }
}
