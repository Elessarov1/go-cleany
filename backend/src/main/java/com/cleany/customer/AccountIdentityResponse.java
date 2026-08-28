package com.cleany.customer;

public record AccountIdentityResponse(
        ExternalIdentityProvider provider,
        boolean linked,
        String username,
        boolean writeAccessAllowed
) {
}
