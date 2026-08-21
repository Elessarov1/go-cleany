package com.cleany.telegram;

import java.util.Collections;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CustomerIdentityProvider;

@Profile("!local")
@RequestScope
@Component
public class TelegramCustomerIdentityProvider implements CustomerIdentityProvider {

    private static final String AUTHORIZATION_SCHEME = "tma";

    private final HttpServletRequest request;
    private final TelegramInitDataValidator validator;

    public TelegramCustomerIdentityProvider(
            HttpServletRequest request,
            TelegramInitDataValidator validator
    ) {
        this.request = request;
        this.validator = validator;
    }

    @Override
    public AuthenticatedCustomerIdentity currentIdentity() {
        var authorizationHeaders = Collections.list(request.getHeaders(HttpHeaders.AUTHORIZATION));
        if (authorizationHeaders.size() != 1) {
            throw new CustomerAuthenticationRequiredException();
        }

        String authorization = authorizationHeaders.getFirst();
        int separatorIndex = authorization.indexOf(' ');
        if (separatorIndex < 1
                || !AUTHORIZATION_SCHEME.equalsIgnoreCase(authorization.substring(0, separatorIndex))) {
            throw new CustomerAuthenticationRequiredException();
        }

        String initData = authorization.substring(separatorIndex + 1);
        if (initData.isBlank() || !initData.equals(initData.strip())) {
            throw new CustomerAuthenticationRequiredException();
        }
        return validator.validate(initData).authenticatedIdentity();
    }
}
