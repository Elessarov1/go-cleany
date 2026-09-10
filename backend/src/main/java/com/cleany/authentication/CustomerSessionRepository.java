package com.cleany.authentication;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface CustomerSessionRepository extends JpaRepository<CustomerSession, UUID> {
    Optional<CustomerSession> findByAccessTokenHash(String accessTokenHash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select session from CustomerSession session where session.refreshTokenHash = :hash or session.previousRefreshTokenHash = :hash")
    Optional<CustomerSession> findByRefreshHashForUpdate(@Param("hash") String hash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select session from CustomerSession session where session.id = :id")
    Optional<CustomerSession> findByIdForUpdate(@Param("id") UUID id);

    @Modifying
    @Query("update CustomerSession session set session.revokedAt = :now, session.revokedReason = :reason where session.customerId = :customerId and session.revokedAt is null")
    int revokeAll(@Param("customerId") long customerId, @Param("now") Instant now, @Param("reason") String reason);

    @Modifying
    @Query("update CustomerSession session set session.revokedAt = :now, session.revokedReason = :reason where session.authenticationIdentityId = :identityId and session.revokedAt is null")
    int revokeByIdentity(@Param("identityId") long identityId, @Param("now") Instant now,
                         @Param("reason") String reason);
}
