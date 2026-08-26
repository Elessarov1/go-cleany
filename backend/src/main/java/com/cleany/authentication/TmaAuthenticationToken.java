package com.cleany.authentication;

import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import com.cleany.customer.AuthenticatedCustomerIdentity;

public class TmaAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthenticatedCustomerIdentity principal;

    public TmaAuthenticationToken(AuthenticatedCustomerIdentity principal) {
        super(List.of());
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public AuthenticatedCustomerIdentity getPrincipal() {
        return principal;
    }

    @Override
    public String getName() {
        return principal.provider() + ":" + principal.externalSubject();
    }
}
