package com.cleany.authentication;

import java.time.Clock;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppleCredentialLifecycleService {
    private final AppleIdentityCredentialRepository credentialRepository;
    private final ExternalProviderJobRepository jobRepository;
    private final Clock clock;

    @Transactional
    public void scheduleCustomerRevocation(long customerId) {
        schedule(credentialRepository.findAllForCustomer(customerId));
    }

    @Transactional
    public void scheduleIdentityRevocation(long identityId) {
        credentialRepository.findById(identityId).ifPresent(credential -> schedule(List.of(credential)));
    }

    @Transactional
    public void retainLinkedCredential(long identityId, String refreshTokenCiphertext) {
        var now = clock.instant();
        AppleIdentityCredential credential = credentialRepository.findById(identityId)
                .orElseGet(() -> new AppleIdentityCredential(identityId, refreshTokenCiphertext, now));
        credential.update(refreshTokenCiphertext, now);
        credentialRepository.save(credential);
    }

    private void schedule(List<AppleIdentityCredential> credentials) {
        var now = clock.instant();
        credentials.forEach(credential -> jobRepository.save(new ExternalProviderJob(
                "APPLE", "REVOKE_REFRESH_TOKEN", credential.getRefreshTokenCiphertext(), now)));
    }
}
