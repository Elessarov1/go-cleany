package com.cleany.authorization;

import org.springframework.stereotype.Service;

import com.cleany.configuration.AdminProperties;
import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.ExternalIdentityProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerRoleBootstrapService {

    private final AdminProperties adminProperties;
    private final PlatformRoleService roleService;

    public void bootstrap(AuthenticatedCustomerIdentity identity, long customerId) {
        if (identity.provider() == ExternalIdentityProvider.GOOGLE
                && identity.emailVerified()
                && adminProperties.containsGoogleEmail(identity.email())) {
            roleService.ensureRole(customerId, PlatformRole.ADMIN);
        }
    }

}
