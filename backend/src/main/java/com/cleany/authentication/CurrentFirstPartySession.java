package com.cleany.authentication;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentFirstPartySession {
    public Optional<UUID> id() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication instanceof BearerAuthenticationToken bearer
                ? Optional.of(bearer.sessionId())
                : Optional.empty();
    }
}
