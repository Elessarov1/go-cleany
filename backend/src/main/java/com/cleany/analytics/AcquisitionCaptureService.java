package com.cleany.analytics;

import java.time.Clock;
import java.time.Instant;

import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcquisitionCaptureService {

    private static final Logger log = LoggerFactory.getLogger(AcquisitionCaptureService.class);
    private static final String PENDING_CODE = AcquisitionCaptureService.class.getName() + ".PENDING_CODE";
    private static final String PENDING_AT = AcquisitionCaptureService.class.getName() + ".PENDING_AT";

    private final AcquisitionCampaignRepository campaignRepository;
    private final AcquisitionCampaignEntryRecorder entryRecorder;
    private final CustomerAttributionService attributionService;
    private final Clock clock;

    public String captureWeb(String publicCode, HttpSession session) {
        AcquisitionCampaign campaign = campaignRepository.findByPublicCode(publicCode).orElse(null);
        if (campaign == null) {
            log.warn("Unknown acquisition campaign publicCode={}", publicCode);
            return AcquisitionTargetService.PLATFORM.targetPath();
        }
        if (!campaign.isActive()) {
            log.info("Inactive acquisition campaign opened campaignId={}", campaign.getId());
            return campaign.getTargetService().targetPath();
        }

        Instant capturedAt = clock.instant();
        recordEntrySafely(campaign.getId(), capturedAt, AcquisitionPlatform.WEB);
        if (session.getAttribute(PENDING_CODE) == null) {
            session.setAttribute(PENDING_CODE, campaign.getPublicCode());
            session.setAttribute(PENDING_AT, capturedAt.toString());
        }
        return campaign.getTargetService().targetPath();
    }

    public void attachPending(long customerId, HttpSession session) {
        if (session == null) {
            return;
        }
        Object codeValue = session.getAttribute(PENDING_CODE);
        Object capturedAtValue = session.getAttribute(PENDING_AT);
        if (!(codeValue instanceof String publicCode) || !(capturedAtValue instanceof String timestamp)) {
            return;
        }
        try {
            AcquisitionCampaign campaign = campaignRepository.findByPublicCode(publicCode)
                    .orElseThrow(() -> new AcquisitionCampaignNotFoundException(
                            "Acquisition campaign not found: " + publicCode
                    ));
            attributionService.attachCampaign(
                    customerId,
                    campaign,
                    Instant.parse(timestamp),
                    AttributionMethod.CAMPAIGN_LINK
            );
        } catch (RuntimeException exception) {
            log.warn("Failed to attach pending acquisition customerId={}", customerId, exception);
            throw exception;
        } finally {
            session.removeAttribute(PENDING_CODE);
            session.removeAttribute(PENDING_AT);
        }
    }

    public AcquisitionCaptureResponse captureTelegram(long customerId, String publicCode) {
        AcquisitionCampaign campaign = campaignRepository.findByPublicCodeAndActiveTrue(publicCode)
                .orElseThrow(() -> new AcquisitionCampaignNotFoundException(
                        "Active acquisition campaign not found: " + publicCode
                ));
        Instant capturedAt = clock.instant();
        recordEntrySafely(campaign.getId(), capturedAt, AcquisitionPlatform.TELEGRAM);
        attributionService.attachCampaign(
                customerId,
                campaign,
                capturedAt,
                AttributionMethod.TELEGRAM_START_PARAMETER
        );
        return new AcquisitionCaptureResponse(campaign.getTargetService().targetPath());
    }

    private void recordEntrySafely(long campaignId, Instant occurredAt, AcquisitionPlatform platform) {
        try {
            entryRecorder.record(campaignId, occurredAt, platform);
        } catch (RuntimeException exception) {
            log.warn("Acquisition campaign entry capture failed campaignId={} platform={}",
                    campaignId, platform, exception);
        }
    }
}
