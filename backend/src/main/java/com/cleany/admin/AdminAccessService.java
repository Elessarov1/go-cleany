package com.cleany.admin;

import org.springframework.stereotype.Service;

import com.cleany.configuration.AdminProperties;
import com.cleany.telegram.CustomerIdentityProvider;

@Service
public class AdminAccessService {

    private final AdminProperties adminProperties;
    private final CustomerIdentityProvider identityProvider;

    public AdminAccessService(
            AdminProperties adminProperties,
            CustomerIdentityProvider identityProvider
    ) {
        this.adminProperties = adminProperties;
        this.identityProvider = identityProvider;
    }

    public boolean isAdmin(long telegramUserId) {
        return adminProperties.contains(telegramUserId);
    }

    public long requireCurrentAdmin() {
        long telegramUserId = identityProvider.currentCustomer().id();
        requireAdmin(telegramUserId);
        return telegramUserId;
    }

    public void requireAdmin(long telegramUserId) {
        if (!isAdmin(telegramUserId)) {
            throw new AdminNotAuthorizedException();
        }
    }
}
