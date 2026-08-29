package com.cleany.analytics;

import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CustomerAccount;
import com.cleany.customer.CustomerAccountMergeService;
import com.cleany.customer.CustomerAccountRepository;

class CustomerAcquisitionMergeIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CustomerAccountMergeService mergeService;

    @Autowired
    private CustomerAccountRepository accountRepository;

    @Autowired
    private AcquisitionCampaignService campaignService;

    @Autowired
    private AcquisitionCampaignRepository campaignRepository;

    @Autowired
    private CustomerAcquisitionRepository acquisitionRepository;

    @Autowired
    private CustomerAttributionService attributionService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from customer_acquisition");
        jdbcTemplate.update("delete from acquisition_campaign_entry");
        jdbcTemplate.update("delete from acquisition_campaign");
        jdbcTemplate.update("delete from customer_account");
    }

    @Test
    @Transactional
    void merge_preservesEarliestFirstTouchAndMovesItToCanonicalCustomer() {
        CustomerAccount target = accountRepository.save(new CustomerAccount(
                Instant.parse("2026-08-20T10:00:00Z")
        ));
        CustomerAccount source = accountRepository.save(new CustomerAccount(
                Instant.parse("2026-08-19T10:00:00Z")
        ));
        AcquisitionCampaignResponse campaignResponse = campaignService.create(
                new CreateAcquisitionCampaignRequest(
                        "merge-first-touch",
                        "Merge first touch",
                        AcquisitionChannel.QR,
                        AcquisitionMedium.QR_PRINT,
                        AcquisitionTargetService.PLATFORM,
                        null
                )
        );
        AcquisitionCampaign campaign = campaignRepository.findById(campaignResponse.id()).orElseThrow();
        attributionService.attachOrganicFallback(
                target.getId(),
                Instant.parse("2026-08-21T10:00:00Z"),
                null
        );
        attributionService.attachCampaign(
                source.getId(),
                campaign,
                Instant.parse("2026-08-18T10:00:00Z"),
                AttributionMethod.CAMPAIGN_LINK
        );

        mergeService.mergeInto(target.getId(), source.getId());

        CustomerAcquisition merged = acquisitionRepository.findById(target.getId()).orElseThrow();
        Assertions.assertAll(
                () -> Assertions.assertFalse(accountRepository.existsById(source.getId())),
                () -> Assertions.assertFalse(acquisitionRepository.existsById(source.getId())),
                () -> Assertions.assertEquals(1, acquisitionRepository.count()),
                () -> Assertions.assertEquals(AcquisitionChannel.QR, merged.getChannel()),
                () -> Assertions.assertEquals(campaign.getId(), merged.getCampaignId()),
                () -> Assertions.assertEquals(
                        Instant.parse("2026-08-18T10:00:00Z"),
                        merged.getFirstTouchAt()
                )
        );
    }
}
