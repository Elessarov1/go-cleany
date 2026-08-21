package com.cleany.telegram;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CustomerIdentityProvider;

@Profile("local")
@Component
public class LocalCustomerIdentityProvider implements CustomerIdentityProvider {

    private final AuthenticatedCustomerIdentity identity;

    public LocalCustomerIdentityProvider(LocalAuthProperties properties) {
        if (properties.telegramUserId() <= 0) {
            throw new IllegalArgumentException("cleany.local-auth.telegram-user-id must be positive");
        }
        identity = new TelegramPrincipal(
                properties.telegramUserId(),
                properties.username(),
                properties.firstName(),
                properties.lastName(),
                properties.languageCode()
        ).authenticatedIdentity();
    }

    @Override
    public AuthenticatedCustomerIdentity currentIdentity() {
        return identity;
    }
}
