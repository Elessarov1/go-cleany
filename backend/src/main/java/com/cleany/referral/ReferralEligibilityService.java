package com.cleany.referral;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cleany.customer.CustomerExternalIdentity;
import com.cleany.customer.CustomerExternalIdentityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReferralEligibilityService {

    private static final Logger log = LoggerFactory.getLogger(ReferralEligibilityService.class);

    private final CustomerExternalIdentityRepository identityRepository;
    private final ReferralEligibilityMarkerStore markerStore;
    private final ReferralIdentityDigest identityDigest;
    private final ReferralAntiAbuseProperties properties;
    private final MeterRegistry meterRegistry;
    private final Clock clock;

    public void markCompletedCustomerIdentities(long customerId) {
        Instant createdAt = clock.instant();
        Instant expiresAt = createdAt.plus(properties.markerRetention());
        identityRepository.findAllByCustomerIdOrderByProvider(customerId).forEach(identity ->
                markerStore.upsert(identity.getProvider(), identityDigest.digest(identity), createdAt, expiresAt));
    }

    public void requireFirstOrderReferralEligible(long customerId) {
        List<CustomerExternalIdentity> identities = identityRepository
                .findAllByCustomerIdOrderByProvider(customerId);
        Instant now = clock.instant();
        for (CustomerExternalIdentity identity : identities) {
            if (markerStore.existsActive(identity.getProvider(), identityDigest.digest(identity), now)) {
                recordDenied(identity);
                throw new ReferralNotApplicableException("Referral code is not applicable");
            }
        }
    }

    public int deleteExpiredMarkers(Instant at, int batchSize) {
        return markerStore.deleteExpired(at, batchSize);
    }

    private void recordDenied(CustomerExternalIdentity identity) {
        try {
            meterRegistry.counter(
                    "loco.referral.eligibility.denied",
                    "provider", identity.getProvider().name(),
                    "reason", "deleted_identity"
            ).increment();
        } catch (RuntimeException exception) {
            log.warn("referral_eligibility_observation_failed provider={} reason=deleted_identity",
                    identity.getProvider());
        }
        log.info("referral_eligibility_denied provider={} reason=deleted_identity",
                identity.getProvider());
    }
}
