package com.cleany.authentication;

import java.util.Collections;
import java.util.UUID;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import com.cleany.customer.AuthenticatedCustomerIdentity;

final class BearerAuthenticationToken extends AbstractAuthenticationToken {
    private final UUID sessionId;
    private final AuthenticatedCustomerIdentity principal;

    BearerAuthenticationToken(UUID sessionId, AuthenticatedCustomerIdentity principal) {
        super(Collections.emptyList());
        this.sessionId = sessionId;
        this.principal = principal;
        setAuthenticated(true);
    }

    UUID sessionId() { return sessionId; }
    @Override public Object getCredentials() { return null; }
    @Override public AuthenticatedCustomerIdentity getPrincipal() { return principal; }
    @Override public String getName() { return "session:" + sessionId; }
}
