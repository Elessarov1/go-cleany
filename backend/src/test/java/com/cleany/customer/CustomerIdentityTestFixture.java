package com.cleany.customer;

import java.time.Instant;

public final class CustomerIdentityTestFixture {

    private CustomerIdentityTestFixture() {
    }

    public static PersistedCustomerIdentity telegramIdentity(
            CustomerAccountRepository accountRepository,
            CustomerExternalIdentityRepository identityRepository,
            Instant createdAt
    ) {
        CustomerAccount account = accountRepository.save(new CustomerAccount(createdAt));
        long telegramUserId = 700000L + account.getId();
        CustomerExternalIdentity identity = identityRepository.save(new CustomerExternalIdentity(
                account.getId(),
                ExternalIdentityProvider.TELEGRAM,
                Long.toString(telegramUserId),
                "customer" + account.getId(),
                "Customer " + account.getId(),
                "ru",
                createdAt
        ));
        if (identity.getId() == null) {
            throw new IllegalStateException("Test external identity was not persisted");
        }
        return new PersistedCustomerIdentity(account.getId(), identity.getId());
    }

    public record PersistedCustomerIdentity(
            long customerId,
            long externalIdentityId
    ) {
    }
}
