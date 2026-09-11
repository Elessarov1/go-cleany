package com.cleany.referral;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cleany.customer.ExternalIdentityProvider;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class ReferralEligibilityMarkerStore {

    static final String SCOPE = "CLEANING_FIRST_ORDER_REFERRAL";

    private final JdbcTemplate jdbcTemplate;

    void upsert(
            ExternalIdentityProvider provider,
            String digest,
            Instant createdAt,
            Instant expiresAt
    ) {
        jdbcTemplate.update("""
                insert into referral_eligibility_marker (
                    provider, identity_digest, scope, created_at, expires_at
                ) values (?, ?, ?, ?, ?)
                on conflict (provider, identity_digest, scope) do update
                   set expires_at = greatest(referral_eligibility_marker.expires_at, excluded.expires_at)
                """, provider.name(), digest, SCOPE,
                Timestamp.from(createdAt), Timestamp.from(expiresAt));
    }

    boolean existsActive(ExternalIdentityProvider provider, String digest, Instant at) {
        Boolean exists = jdbcTemplate.queryForObject("""
                select exists (
                    select 1
                      from referral_eligibility_marker
                     where provider = ?
                       and identity_digest = ?
                       and scope = ?
                       and expires_at > ?
                )
                """, Boolean.class, provider.name(), digest, SCOPE, Timestamp.from(at));
        return Boolean.TRUE.equals(exists);
    }

    int deleteExpired(Instant at, int batchSize) {
        return jdbcTemplate.update("""
                with expired as (
                    select id
                      from referral_eligibility_marker
                     where expires_at <= ?
                     order by expires_at, id
                     for update skip locked
                     limit ?
                )
                delete from referral_eligibility_marker marker
                 using expired
                 where marker.id = expired.id
                """, Timestamp.from(at), batchSize);
    }
}
