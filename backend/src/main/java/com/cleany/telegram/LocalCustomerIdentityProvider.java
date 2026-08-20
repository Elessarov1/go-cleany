package com.cleany.telegram;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
public class LocalCustomerIdentityProvider implements CustomerIdentityProvider {

    private final TelegramPrincipal principal;

    public LocalCustomerIdentityProvider(LocalAuthProperties properties) {
        if (properties.telegramUserId() <= 0) {
            throw new IllegalArgumentException("cleany.local-auth.telegram-user-id must be positive");
        }
        principal = new TelegramPrincipal(
                properties.telegramUserId(),
                properties.username(),
                properties.firstName(),
                properties.lastName(),
                properties.languageCode()
        );
    }

    @Override
    public TelegramPrincipal currentCustomer() {
        return principal;
    }
}
