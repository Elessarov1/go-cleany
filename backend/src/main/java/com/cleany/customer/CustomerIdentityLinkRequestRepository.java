package com.cleany.customer;

import java.time.Instant;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerIdentityLinkRequestRepository
        extends JpaRepository<CustomerIdentityLinkRequest, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select request from CustomerIdentityLinkRequest request where request.tokenHash = :tokenHash")
    Optional<CustomerIdentityLinkRequest> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);

    @Modifying(flushAutomatically = true)
    @Query("""
            update CustomerIdentityLinkRequest request
               set request.consumedAt = :consumedAt
             where request.targetCustomerId = :customerId
               and request.provider = :provider
               and request.consumedAt is null
            """)
    int consumeOutstanding(
            @Param("customerId") long customerId,
            @Param("provider") ExternalIdentityProvider provider,
            @Param("consumedAt") Instant consumedAt
    );
}
