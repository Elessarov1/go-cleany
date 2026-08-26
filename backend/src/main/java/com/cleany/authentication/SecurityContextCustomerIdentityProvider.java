package com.cleany.authentication;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CustomerIdentityProvider;
import lombok.RequiredArgsConstructor;

@Profile("!local")
@Component
@RequiredArgsConstructor
public class SecurityContextCustomerIdentityProvider implements CustomerIdentityProvider {

    private final OidcCustomerIdentityMapper oidcMapper;

    @Override
    public AuthenticatedCustomerIdentity currentIdentity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomerAuthenticationRequiredException();
        }
        if (authentication.getPrincipal() instanceof AuthenticatedCustomerIdentity identity) {
            return identity;
        }
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            return oidcMapper.map(oidcUser);
        }
        throw new CustomerAuthenticationRequiredException();
    }
}
