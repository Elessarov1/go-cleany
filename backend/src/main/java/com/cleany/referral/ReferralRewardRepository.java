package com.cleany.referral;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ReferralRewardRepository extends JpaRepository<ReferralReward, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ReferralReward> findFirstByCustomerIdAndStatusOrderByCreatedAtAsc(
            long customerId,
            ReferralRewardStatus status
    );

    long countByCustomerIdAndStatus(long customerId, ReferralRewardStatus status);

    boolean existsByCustomerIdAndStatus(long customerId, ReferralRewardStatus status);

    boolean existsBySourceOrderId(long sourceOrderId);
}
