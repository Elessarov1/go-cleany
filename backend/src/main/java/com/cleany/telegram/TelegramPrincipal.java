package com.cleany.telegram;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.ExternalIdentityProvider;

public record TelegramPrincipal(
        long id,
        String username,
        String firstName,
        String lastName,
        String languageCode
) {

    public String displayName() {
        var name = Stream.of(firstName, lastName)
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining(" "));
        return name.isBlank() ? "Telegram user " + id : name;
    }

    public AuthenticatedCustomerIdentity authenticatedIdentity() {
        return new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.TELEGRAM,
                Long.toString(id),
                username,
                displayName(),
                languageCode
        );
    }
}
