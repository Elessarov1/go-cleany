package com.cleany.customer;

import jakarta.validation.constraints.NotNull;

public record CreateIdentityLinkRequest(@NotNull ExternalIdentityProvider provider) {
}
