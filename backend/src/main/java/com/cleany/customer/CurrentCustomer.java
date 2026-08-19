package com.cleany.customer;

public record CurrentCustomer(
        long id,
        long telegramUserId,
        String telegramUsername,
        String displayName
) {
}
