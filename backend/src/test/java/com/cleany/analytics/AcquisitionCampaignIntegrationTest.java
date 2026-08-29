package com.cleany.analytics;

import java.time.Instant;
import java.sql.Timestamp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cleany.base.BaseIntegrationTest;

class AcquisitionCampaignIntegrationTest extends BaseIntegrationTest {

    private static final String PARTNER_NAME = "Analytics test partner";

    @Autowired
    private AcquisitionCampaignService campaignService;

    @Autowired
    private AcquisitionCampaignRepository campaignRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void cleanAnalyticsData() {
        jdbcTemplate.update("delete from acquisition_campaign_entry");
        jdbcTemplate.update("delete from customer_acquisition");
        jdbcTemplate.update("delete from acquisition_campaign");
        jdbcTemplate.update("delete from referral_partner where name = ?", PARTNER_NAME);
    }

    @Test
    void createUpdateAndList_preserveStablePublicCodeAndPartnerReference() {
        long partnerId = createPartner();

        AcquisitionCampaignResponse created = campaignService.create(new CreateAcquisitionCampaignRequest(
                "mh-mag-2026-09-a",
                "Mahmutlar magnets / September 2026 / batch A",
                AcquisitionChannel.QR,
                AcquisitionMedium.QR_MAGNET,
                AcquisitionTargetService.RENTAL,
                partnerId
        ));

        AcquisitionCampaignResponse disabled = campaignService.update(
                created.id(),
                new UpdateAcquisitionCampaignRequest(
                        "Mahmutlar magnets / September 2026",
                        AcquisitionChannel.QR,
                        AcquisitionMedium.QR_MAGNET,
                        AcquisitionTargetService.PLATFORM,
                        partnerId,
                        false
                )
        );

        AcquisitionCampaignResponse reactivated = campaignService.update(
                created.id(),
                new UpdateAcquisitionCampaignRequest(
                        disabled.name(),
                        disabled.channel(),
                        disabled.medium(),
                        disabled.targetService(),
                        disabled.partnerId(),
                        true
                )
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals("mh-mag-2026-09-a", created.publicCode()),
                () -> Assertions.assertEquals("/a/mh-mag-2026-09-a", created.trackingPath()),
                () -> Assertions.assertEquals("/rent", created.targetPath()),
                () -> Assertions.assertEquals(PARTNER_NAME, created.partnerName()),
                () -> Assertions.assertFalse(disabled.active()),
                () -> Assertions.assertNotNull(disabled.disabledAt()),
                () -> Assertions.assertEquals("mh-mag-2026-09-a", disabled.publicCode()),
                () -> Assertions.assertEquals("/", disabled.targetPath()),
                () -> Assertions.assertTrue(reactivated.active()),
                () -> Assertions.assertNull(reactivated.disabledAt()),
                () -> Assertions.assertEquals(1, campaignService.getCampaigns().size()),
                () -> Assertions.assertEquals(
                        "mh-mag-2026-09-a",
                        campaignRepository.findById(created.id()).orElseThrow().getPublicCode()
                )
        );
    }

    @Test
    void duplicateCodeAndUnknownPartner_areRejected() {
        CreateAcquisitionCampaignRequest request = new CreateAcquisitionCampaignRequest(
                "kestel-sticker-gym-x",
                "Kestel sticker / Gym X",
                AcquisitionChannel.QR,
                AcquisitionMedium.QR_STICKER,
                AcquisitionTargetService.CLEANING,
                null
        );
        campaignService.create(request);

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        InvalidAcquisitionCampaignException.class,
                        () -> campaignService.create(request)
                ),
                () -> Assertions.assertThrows(
                        InvalidAcquisitionCampaignException.class,
                        () -> campaignService.create(new CreateAcquisitionCampaignRequest(
                                "partner-office",
                                "Partner office",
                                AcquisitionChannel.PARTNER,
                                AcquisitionMedium.PARTNER_LINK,
                                AcquisitionTargetService.PLATFORM,
                                Long.MAX_VALUE
                        ))
                ),
                () -> Assertions.assertThrows(
                        InvalidAcquisitionCampaignException.class,
                        () -> campaignService.create(new CreateAcquisitionCampaignRequest(
                                "organic-is-derived",
                                "Invalid organic campaign",
                                AcquisitionChannel.ORGANIC,
                                AcquisitionMedium.DIRECT_LINK,
                                AcquisitionTargetService.PLATFORM,
                                null
                        ))
                )
        );
    }

    private long createPartner() {
        return jdbcTemplate.queryForObject(
                "insert into referral_partner(name, active, created_at) values (?, true, ?) returning id",
                Long.class,
                PARTNER_NAME,
                Timestamp.from(Instant.parse("2026-09-01T09:00:00Z"))
        );
    }
}
