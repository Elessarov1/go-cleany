package com.cleany.authentication;

import java.util.Set;

import com.cleany.authorization.PlatformRole;
import com.cleany.customer.ExternalIdentityProvider;

public record CurrentAuthenticationResponse(
        boolean authenticated,
        Long customerId,
        String displayName,
        ExternalIdentityProvider provider,
        Set<PlatformRole> roles
) {

    static CurrentAuthenticationResponse anonymous() {
        return new CurrentAuthenticationResponse(false, null, null, null, Set.of());
    }
}
