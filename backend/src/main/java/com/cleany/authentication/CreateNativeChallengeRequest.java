package com.cleany.authentication;

import jakarta.validation.constraints.NotNull;

import com.cleany.customer.ExternalIdentityProvider;

public record CreateNativeChallengeRequest(
        @NotNull ExternalIdentityProvider provider,
        @NotNull NativeClientType clientType
) {
}
