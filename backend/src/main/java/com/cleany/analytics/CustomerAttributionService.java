package com.cleany.analytics;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.catalog.PlatformService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerAttributionService {

    private static final Logger log = LoggerFactory.getLogger(CustomerAttributionService.class);

    private final JdbcTemplate jdbcTemplate;
    private final AcquisitionCampaignRepository campaignRepository;
    private final Clock clock;

    @Transactional
    public boolean attachCampaign(
            long customerId,
            AcquisitionCampaign campaign,
            Instant firstTouchAt,
            AttributionMethod method
    ) {
        PlatformService firstTouchService = switch (campaign.getTargetService()) {
            case PLATFORM -> null;
            case CLEANING -> PlatformService.CLEANING;
            case RENTAL -> PlatformService.RENTAL;
        };
        return insertFirstTouch(
                customerId,
                campaign.getChannel(),
                campaign.getId(),
                firstTouchAt,
                firstTouchService,
                method
        );
    }

    @Transactional
    public boolean attachPartnerCode(
            long customerId,
            long partnerId,
            Instant firstTouchAt,
            PlatformService firstTouchService
    ) {
        Long campaignId = campaignRepository
                .findFirstByPartnerIdAndMediumAndActiveTrueOrderByCreatedAtAsc(
                        partnerId,
                        AcquisitionMedium.REFERRAL_CODE
                )
                .map(AcquisitionCampaign::getId)
                .orElse(null);
        return insertFirstTouch(
                customerId,
                AcquisitionChannel.PARTNER,
                campaignId,
                firstTouchAt,
                firstTouchService,
                AttributionMethod.PARTNER_CODE
        );
    }

    @Transactional
    public boolean attachCustomerReferralCode(
            long customerId,
            Instant firstTouchAt,
            PlatformService firstTouchService
    ) {
        return insertFirstTouch(
                customerId,
                AcquisitionChannel.CUSTOMER_REFERRAL,
                null,
                firstTouchAt,
                firstTouchService,
                AttributionMethod.CUSTOMER_REFERRAL_CODE
        );
    }

    @Transactional
    public boolean attachOrganicFallback(
            long customerId,
            Instant firstTouchAt,
            PlatformService firstTouchService
    ) {
        return insertFirstTouch(
                customerId,
                AcquisitionChannel.ORGANIC,
                null,
                firstTouchAt,
                firstTouchService,
                AttributionMethod.ORGANIC_FALLBACK
        );
    }

    private boolean insertFirstTouch(
            long customerId,
            AcquisitionChannel channel,
            Long campaignId,
            Instant firstTouchAt,
            PlatformService firstTouchService,
            AttributionMethod method
    ) {
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(firstTouchAt, "firstTouchAt");
        Objects.requireNonNull(method, "method");
        int inserted = jdbcTemplate.update("""
                insert into customer_acquisition(
                    customer_id, channel, campaign_id, first_touch_at,
                    first_touch_service, attribution_method, created_at
                ) values (?, ?, ?, ?, ?, ?, ?)
                on conflict (customer_id) do nothing
                """,
                customerId,
                channel.name(),
                campaignId,
                OffsetDateTime.ofInstant(firstTouchAt, ZoneOffset.UTC),
                firstTouchService == null ? null : firstTouchService.name(),
                method.name(),
                OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC)
        );
        if (inserted == 1) {
            log.info(
                    "Attached first-touch acquisition customerId={} campaignId={} method={}",
                    customerId,
                    campaignId,
                    method
            );
            return true;
        }
        log.debug(
                "Ignored repeated first-touch acquisition customerId={} campaignId={}",
                customerId,
                campaignId
        );
        return false;
    }
}
