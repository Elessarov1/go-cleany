package com.cleany.customer;

import jakarta.validation.constraints.Size;

public record VerifyIdentityLinkRequest(
        @Size(max = 16_384) String identityToken,
        @Size(max = 4_096) String authorizationCode
) {
}
