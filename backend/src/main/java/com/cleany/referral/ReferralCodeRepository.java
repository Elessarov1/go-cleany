package com.cleany.referral;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralCodeRepository extends JpaRepository<ReferralCode, Long> {

    Optional<ReferralCode> findByCodeIgnoreCaseAndActiveTrue(String code);

    Optional<ReferralCode> findFirstByCustomerIdAndActiveTrueOrderByCreatedAtAsc(long customerId);

    Optional<ReferralCode> findByPartnerIdAndActiveTrue(long partnerId);

    boolean existsByCodeIgnoreCase(String code);
}
