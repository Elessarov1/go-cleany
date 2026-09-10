package com.cleany.customer;

public record AccountIdentityResponse(
        Long identityId,
        ExternalIdentityProvider provider,
        String issuer,
        boolean linked,
        String username,
        boolean writeAccessAllowed
) {
    public AccountIdentityResponse(ExternalIdentityProvider provider, boolean linked,
                                   String username, boolean writeAccessAllowed) {
        this(null, provider, null, linked, username, writeAccessAllowed);
    }
}
