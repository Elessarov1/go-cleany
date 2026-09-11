package com.cleany.referral;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReferralRewardRepository extends JpaRepository<ReferralReward, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ReferralReward> findFirstByCustomerIdAndStatusOrderByCreatedAtAsc(
            long customerId,
            ReferralRewardStatus status
    );

    long countByCustomerIdAndStatus(long customerId, ReferralRewardStatus status);

    boolean existsByCustomerIdAndStatus(long customerId, ReferralRewardStatus status);

    boolean existsBySourceOrderId(long sourceOrderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select reward from ReferralReward reward
             where reward.customerId = :customerId
               and reward.status in :statuses
             order by reward.createdAt, reward.id
            """)
    List<ReferralReward> findAllRevocableByCustomerIdForUpdate(
            @Param("customerId") long customerId,
            @Param("statuses") List<ReferralRewardStatus> statuses
    );
}
