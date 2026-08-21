package com.cleany.admin;

import org.springframework.stereotype.Service;

import com.cleany.configuration.AdminProperties;
import com.cleany.customer.CustomerIdentityProvider;
import com.cleany.customer.ExternalIdentityProvider;

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
        var identity = identityProvider.currentIdentity();
        if (identity.provider() != ExternalIdentityProvider.TELEGRAM) {
            throw new AdminNotAuthorizedException();
        }
        long telegramUserId;
        try {
            telegramUserId = Long.parseLong(identity.externalSubject());
        } catch (NumberFormatException exception) {
            throw new AdminNotAuthorizedException();
        }
        requireAdmin(telegramUserId);
        return telegramUserId;
    }

    public void requireAdmin(long telegramUserId) {
        if (!isAdmin(telegramUserId)) {
            throw new AdminNotAuthorizedException();
        }
    }
}
