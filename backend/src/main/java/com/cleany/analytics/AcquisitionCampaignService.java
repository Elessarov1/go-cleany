package com.cleany.analytics;

import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.referral.ReferralPartner;
import com.cleany.referral.ReferralPartnerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcquisitionCampaignService {

    private final AcquisitionCampaignRepository campaignRepository;
    private final ReferralPartnerRepository partnerRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<AcquisitionCampaignResponse> getCampaigns() {
        List<AcquisitionCampaign> campaigns = campaignRepository.findAllByOrderByCreatedAtDesc();
        Set<Long> partnerIds = campaigns.stream()
                .map(AcquisitionCampaign::getPartnerId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> partnerNames = partnerIds.isEmpty()
                ? Collections.emptyMap()
                : partnerRepository.findAllById(partnerIds).stream()
                        .collect(Collectors.toMap(ReferralPartner::getId, ReferralPartner::getName));
        return campaigns.stream()
                .map(campaign -> response(campaign, partnerNames.get(campaign.getPartnerId())))
                .toList();
    }

    @Transactional
    public AcquisitionCampaignResponse create(CreateAcquisitionCampaignRequest request) {
        requireTrackableChannel(request.channel());
        String publicCode = request.publicCode().trim();
        if (campaignRepository.existsByPublicCode(publicCode)) {
            throw new InvalidAcquisitionCampaignException("Campaign public code already exists");
        }
        ReferralPartner partner = requirePartner(request.partnerId());
        AcquisitionCampaign campaign = new AcquisitionCampaign(
                publicCode,
                request.name().trim(),
                request.channel(),
                request.medium(),
                request.targetService(),
                request.partnerId(),
                clock.instant()
        );
        try {
            campaignRepository.saveAndFlush(campaign);
        } catch (DataIntegrityViolationException exception) {
            throw new InvalidAcquisitionCampaignException("Campaign public code already exists");
        }
        return response(campaign, partner == null ? null : partner.getName());
    }

    @Transactional
    public AcquisitionCampaignResponse update(long campaignId, UpdateAcquisitionCampaignRequest request) {
        requireTrackableChannel(request.channel());
        AcquisitionCampaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new AcquisitionCampaignNotFoundException(
                        "Acquisition campaign not found: " + campaignId
                ));
        ReferralPartner partner = requirePartner(request.partnerId());
        campaign.update(
                request.name().trim(),
                request.channel(),
                request.medium(),
                request.targetService(),
                request.partnerId(),
                request.active(),
                clock.instant()
        );
        return response(campaign, partner == null ? null : partner.getName());
    }

    private ReferralPartner requirePartner(Long partnerId) {
        if (partnerId == null) {
            return null;
        }
        return partnerRepository.findById(partnerId)
                .orElseThrow(() -> new InvalidAcquisitionCampaignException(
                        "Referral partner not found: " + partnerId
                ));
    }

    private static void requireTrackableChannel(AcquisitionChannel channel) {
        if (channel == AcquisitionChannel.ORGANIC) {
            throw new InvalidAcquisitionCampaignException(
                    "ORGANIC is derived automatically and cannot be assigned to a campaign"
            );
        }
    }

    private static AcquisitionCampaignResponse response(
            AcquisitionCampaign campaign,
            String partnerName
    ) {
        return new AcquisitionCampaignResponse(
                campaign.getId(),
                campaign.getPublicCode(),
                campaign.getName(),
                campaign.getChannel(),
                campaign.getMedium(),
                campaign.getTargetService(),
                campaign.getPartnerId(),
                partnerName,
                campaign.isActive(),
                campaign.getCreatedAt(),
                campaign.getDisabledAt(),
                "/a/" + campaign.getPublicCode(),
                campaign.getTargetService().targetPath()
        );
    }
}
