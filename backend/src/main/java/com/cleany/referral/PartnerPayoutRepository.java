package com.cleany.referral;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerPayoutRepository extends JpaRepository<PartnerPayout, Long> {

    boolean existsBySourceOrderId(long sourceOrderId);

    List<PartnerPayout> findAllByOrderByCreatedAtDesc();
}
