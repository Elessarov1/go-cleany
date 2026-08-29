package com.cleany.analytics;

import java.time.Instant;
import java.sql.Timestamp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;

import com.cleany.base.BaseIntegrationTest;

class AcquisitionCaptureIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AcquisitionCampaignService campaignService;

    @Autowired
    private AcquisitionCaptureService captureService;

    @Autowired
    private CustomerAcquisitionRepository acquisitionRepository;

    @Autowired
    private AcquisitionCampaignEntryRepository entryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long customerId;

    @BeforeEach
    @AfterEach
    void cleanAnalyticsData() {
        jdbcTemplate.update("delete from acquisition_campaign_entry");
        jdbcTemplate.update("delete from customer_acquisition");
        jdbcTemplate.update("delete from acquisition_campaign");
        if (customerId != null) {
            jdbcTemplate.update("delete from customer_account where id = ?", customerId);
            customerId = null;
        }
    }

    @Test
    void anonymousWebCapture_survivesLoginAndSecondCampaignDoesNotOverwriteFirstTouch() {
        AcquisitionCampaignResponse first = createCampaign(
                "mh-mag-a",
                AcquisitionChannel.QR,
                AcquisitionMedium.QR_MAGNET,
                AcquisitionTargetService.RENTAL
        );
        createCampaign(
                "kestel-direct-b",
                AcquisitionChannel.DIRECT_CAMPAIGN,
                AcquisitionMedium.DIRECT_LINK,
                AcquisitionTargetService.CLEANING
        );
        MockHttpSession session = new MockHttpSession();

        String firstTarget = captureService.captureWeb(first.publicCode(), session);
        String secondTarget = captureService.captureWeb("kestel-direct-b", session);
        customerId = createCustomer();
        captureService.attachPending(customerId, session);

        CustomerAcquisition acquisition = acquisitionRepository.findById(customerId).orElseThrow();
        captureService.captureWeb("kestel-direct-b", session);
        captureService.attachPending(customerId, session);
        CustomerAcquisition unchanged = acquisitionRepository.findById(customerId).orElseThrow();

        Assertions.assertAll(
                () -> Assertions.assertEquals("/rent", firstTarget),
                () -> Assertions.assertEquals("/cleaning", secondTarget),
                () -> Assertions.assertEquals(AcquisitionChannel.QR, acquisition.getChannel()),
                () -> Assertions.assertEquals(first.id(), acquisition.getCampaignId()),
                () -> Assertions.assertEquals(AttributionMethod.CAMPAIGN_LINK, acquisition.getAttributionMethod()),
                () -> Assertions.assertEquals(first.id(), unchanged.getCampaignId()),
                () -> Assertions.assertEquals(3, entryRepository.count())
        );
    }

    @Test
    void telegramCapture_recordsTelegramEntryAndAttachesCanonicalCustomer() {
        AcquisitionCampaignResponse campaign = createCampaign(
                "partner-office-link",
                AcquisitionChannel.PARTNER,
                AcquisitionMedium.PARTNER_LINK,
                AcquisitionTargetService.PLATFORM
        );
        customerId = createCustomer();

        AcquisitionCaptureResponse response = captureService.captureTelegram(customerId, campaign.publicCode());
        CustomerAcquisition acquisition = acquisitionRepository.findById(customerId).orElseThrow();
        AcquisitionCampaignEntry entry = entryRepository.findAll().getFirst();

        Assertions.assertAll(
                () -> Assertions.assertEquals("/", response.targetPath()),
                () -> Assertions.assertEquals(AcquisitionChannel.PARTNER, acquisition.getChannel()),
                () -> Assertions.assertEquals(
                        AttributionMethod.TELEGRAM_START_PARAMETER,
                        acquisition.getAttributionMethod()
                ),
                () -> Assertions.assertNull(acquisition.getFirstTouchService()),
                () -> Assertions.assertEquals(AcquisitionPlatform.TELEGRAM, entry.getPlatform()),
                () -> Assertions.assertEquals(campaign.id(), entry.getCampaignId())
        );
    }

    @Test
    void unknownAndInactiveCampaigns_redirectSafelyWithoutTracking() {
        AcquisitionCampaignResponse campaign = createCampaign(
                "inactive-rental",
                AcquisitionChannel.QR,
                AcquisitionMedium.QR_PRINT,
                AcquisitionTargetService.RENTAL
        );
        campaignService.update(campaign.id(), new UpdateAcquisitionCampaignRequest(
                campaign.name(),
                campaign.channel(),
                campaign.medium(),
                campaign.targetService(),
                null,
                false
        ));
        MockHttpSession session = new MockHttpSession();

        Assertions.assertAll(
                () -> Assertions.assertEquals("/rent", captureService.captureWeb("inactive-rental", session)),
                () -> Assertions.assertEquals("/", captureService.captureWeb("unknown-campaign", session)),
                () -> Assertions.assertEquals(0, entryRepository.count())
        );
    }

    private AcquisitionCampaignResponse createCampaign(
            String publicCode,
            AcquisitionChannel channel,
            AcquisitionMedium medium,
            AcquisitionTargetService targetService
    ) {
        return campaignService.create(new CreateAcquisitionCampaignRequest(
                publicCode,
                "Test " + publicCode,
                channel,
                medium,
                targetService,
                null
        ));
    }

    private long createCustomer() {
        return jdbcTemplate.queryForObject(
                "insert into customer_account(created_at) values (?) returning id",
                Long.class,
                Timestamp.from(Instant.parse("2026-09-02T10:00:00Z"))
        );
    }
}
