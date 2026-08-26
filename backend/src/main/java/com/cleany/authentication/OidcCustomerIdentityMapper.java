package com.cleany.authentication;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.ExternalIdentityProvider;

@Component
public class OidcCustomerIdentityMapper {

    public AuthenticatedCustomerIdentity map(OidcUser user) {
        String subject = user.getSubject();
        String displayName = firstPresent(
                user.getClaimAsString("name"),
                user.getClaimAsString("given_name"),
                subject
        );
        return new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.GOOGLE,
                subject,
                firstPresent(
                        user.getClaimAsString("preferred_username"),
                        user.getEmail()
                ),
                displayName,
                user.getClaimAsString("locale"),
                user.getEmail(),
                Boolean.TRUE.equals(user.getEmailVerified())
        );
    }

    private static String firstPresent(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }
}
