package com.cleany.customer;

import java.util.List;

public record AccountIdentitiesResponse(List<AccountIdentityResponse> identities) {

    public AccountIdentitiesResponse {
        identities = List.copyOf(identities);
    }
}
