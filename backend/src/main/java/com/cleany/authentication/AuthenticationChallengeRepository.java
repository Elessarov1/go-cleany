package com.cleany.authentication;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface AuthenticationChallengeRepository extends JpaRepository<AuthenticationChallenge, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select challenge from AuthenticationChallenge challenge where challenge.id = :id")
    Optional<AuthenticationChallenge> findByIdForUpdate(@Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select challenge from AuthenticationChallenge challenge where challenge.provider = :provider and challenge.nonceHash = :nonceHash")
    Optional<AuthenticationChallenge> findByProviderAndNonceHashForUpdate(
            @Param("provider") com.cleany.customer.ExternalIdentityProvider provider,
            @Param("nonceHash") String nonceHash
    );
}
