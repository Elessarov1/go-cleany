package com.cleany.transfer;

import java.time.Instant;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransferDriverLinkTokenRepository
        extends JpaRepository<TransferDriverLinkToken, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select token
              from TransferDriverLinkToken token
              join fetch token.driver
             where token.tokenHash = :tokenHash
            """)
    Optional<TransferDriverLinkToken> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);

    @Modifying
    @Query("""
            update TransferDriverLinkToken token
               set token.consumedAt = :consumedAt
             where token.driver.id = :driverId
               and token.consumedAt is null
            """)
    int consumeOutstanding(@Param("driverId") long driverId, @Param("consumedAt") Instant consumedAt);
}
