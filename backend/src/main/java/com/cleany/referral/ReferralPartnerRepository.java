package com.cleany.referral;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralPartnerRepository extends JpaRepository<ReferralPartner, Long> {

    List<ReferralPartner> findAllByOrderByCreatedAtDesc();
}
