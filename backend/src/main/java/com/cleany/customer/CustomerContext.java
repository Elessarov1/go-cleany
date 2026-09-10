package com.cleany.customer;

public record CustomerContext(
        long customerId,
        String displayName,
        String languageCode
) {
    public CustomerContext {
        if (customerId <= 0) {
            throw new IllegalArgumentException("customerId must be positive");
        }
    }
}
