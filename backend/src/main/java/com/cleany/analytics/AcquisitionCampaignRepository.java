package com.cleany.analytics;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AcquisitionCampaignRepository extends JpaRepository<AcquisitionCampaign, Long> {

    Optional<AcquisitionCampaign> findByPublicCode(String publicCode);

    Optional<AcquisitionCampaign> findByPublicCodeAndActiveTrue(String publicCode);

    List<AcquisitionCampaign> findAllByOrderByCreatedAtDesc();

    Optional<AcquisitionCampaign> findFirstByPartnerIdAndMediumAndActiveTrueOrderByCreatedAtAsc(
            long partnerId,
            AcquisitionMedium medium
    );

    boolean existsByPublicCode(String publicCode);
}
