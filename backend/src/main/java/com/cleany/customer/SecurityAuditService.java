package com.cleany.customer;

import java.time.Clock;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class SecurityAuditService {
    private final SecurityAuditEventRepository repository;
    private final Clock clock;

    void success(long customerId, String type, ExternalIdentityProvider provider, Long identityId) {
        repository.save(new SecurityAuditEvent(customerId, type, provider, identityId,
                null, "SUCCESS", null, clock.instant()));
    }
}
