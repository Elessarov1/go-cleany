package com.cleany.authentication;

import java.time.Clock;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class AppleCredentialRevocationQueue {
    private final ExternalProviderJobRepository repository;
    private final Clock clock;

    @Transactional
    List<Long> claim() {
        var now = clock.instant();
        List<ExternalProviderJob> jobs = repository.claimable(now, 10);
        jobs.forEach(job -> job.claim(now));
        return jobs.stream().map(ExternalProviderJob::getId).toList();
    }

    @Transactional(readOnly = true)
    String payload(long id) {
        return repository.findById(id).orElseThrow().getPayloadCiphertext();
    }

    @Transactional
    void delivered(long id) {
        repository.findById(id).ifPresent(job -> job.delivered(clock.instant()));
    }

    @Transactional
    void failed(long id, boolean retryable, String code) {
        repository.findById(id).ifPresent(job -> job.failed(clock.instant(), code, retryable));
    }
}
