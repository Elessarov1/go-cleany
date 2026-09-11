package com.cleany.referral;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralCodeRepository extends JpaRepository<ReferralCode, Long> {

    Optional<ReferralCode> findByCodeIgnoreCaseAndActiveTrue(String code);

    Optional<ReferralCode> findFirstByCustomerIdAndActiveTrueOrderByCreatedAtAsc(long customerId);

    List<ReferralCode> findAllByCustomerIdAndActiveTrueOrderByCreatedAtAsc(long customerId);

    Optional<ReferralCode> findByPartnerIdAndActiveTrue(long partnerId);

    boolean existsByCodeIgnoreCase(String code);
}
