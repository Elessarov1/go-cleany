package com.cleany.admin;

import org.springframework.stereotype.Service;

import com.cleany.authorization.PlatformRole;
import com.cleany.authorization.PlatformRoleService;
import com.cleany.customer.CustomerAccountService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAccessService {

    private final CustomerAccountService customerAccountService;
    private final PlatformRoleService roleService;

    public boolean isAdmin(long customerId) {
        return roleService.hasRole(customerId, PlatformRole.ADMIN);
    }

    public long requireCurrentAdmin() {
        long customerId = customerAccountService.currentCustomer().customerId();
        requireAdmin(customerId);
        return customerId;
    }

    public void requireAdmin(long customerId) {
        if (!isAdmin(customerId)) {
            throw new AdminNotAuthorizedException();
        }
    }
}
