package com.cleany.admin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.authorization.PlatformRole;
import com.cleany.authorization.PlatformRoleService;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.ExternalIdentityProvider;

class AdminAccessServiceTest {

    private static final long CUSTOMER_ID = 77L;

    @Test
    void currentCustomerWithAdminRole_accessGrantedRegardlessOfProvider() {
        CustomerAccountService customerAccountService = Mockito.mock(CustomerAccountService.class);
        PlatformRoleService roleService = Mockito.mock(PlatformRoleService.class);
        Mockito.when(customerAccountService.currentCustomer()).thenReturn(customer(
                ExternalIdentityProvider.GOOGLE,
                "google-subject"
        ));
        Mockito.when(roleService.hasRole(CUSTOMER_ID, PlatformRole.ADMIN)).thenReturn(true);
        var service = new AdminAccessService(customerAccountService, roleService);

        Assertions.assertEquals(CUSTOMER_ID, service.requireCurrentAdmin());
    }

    @Test
    void currentCustomerWithoutAdminRole_accessRejected() {
        CustomerAccountService customerAccountService = Mockito.mock(CustomerAccountService.class);
        PlatformRoleService roleService = Mockito.mock(PlatformRoleService.class);
        Mockito.when(customerAccountService.currentCustomer()).thenReturn(customer(
                ExternalIdentityProvider.TELEGRAM,
                "900001"
        ));
        var service = new AdminAccessService(customerAccountService, roleService);

        Assertions.assertThrows(AdminNotAuthorizedException.class, service::requireCurrentAdmin);
    }

    private static CurrentCustomer customer(
            ExternalIdentityProvider provider,
            String subject
    ) {
        return new CurrentCustomer(
                CUSTOMER_ID,
                88L,
                provider,
                subject,
                "alex",
                "Alex",
                "ru"
        );
    }
}
