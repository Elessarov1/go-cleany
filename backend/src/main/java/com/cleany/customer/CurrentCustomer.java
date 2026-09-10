package com.cleany.customer;

public record CurrentCustomer(
        long customerId,
        long externalIdentityId,
        ExternalIdentityProvider provider,
        String externalSubject,
        String username,
        String displayName,
        String languageCode
) {
    public CustomerContext customerContext() {
        return new CustomerContext(customerId, displayName, languageCode);
    }
}
