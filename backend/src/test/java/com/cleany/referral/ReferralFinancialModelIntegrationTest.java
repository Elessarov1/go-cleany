package com.cleany.referral;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CustomerAccount;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.finance.AcquisitionSource;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderService;
import com.cleany.telegram.TelegramInitDataTestFactory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ReferralFinancialModelIntegrationTest extends BaseIntegrationTest {

    private static final String BOT_TOKEN = "123456789:test-token";
    private static final long CLEANER_ID = 123456789L;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CustomerAccountRepository customerRepository;

    @Autowired
    private CustomerExternalIdentityRepository identityRepository;

    @Autowired
    private CleaningOrderRepository orderRepository;

    @Autowired
    private CleaningOrderService orderService;

    @Autowired
    private ReferralCodeRepository codeRepository;

    @Autowired
    private ReferralPartnerRepository partnerRepository;

    @Autowired
    private ReferralRewardRepository rewardRepository;

    @Autowired
    private PartnerPayoutRepository payoutRepository;

    @Autowired
    private ReferralService referralService;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        payoutRepository.deleteAll();
        rewardRepository.deleteAll();
        orderRepository.deleteAll();
        codeRepository.deleteAll();
        identityRepository.deleteAll();
        partnerRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void customerReferral_firstCompletedOrderCreatesSeparateReferrerReward() throws Exception {
        CustomerAccount referrer = customerRepository.save(new CustomerAccount(Instant.now()));
        codeRepository.save(ReferralCode.customer("FRIEND10", referrer.getId(), Instant.now()));

        createOrder(800001L, "FRIEND10")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.basePrice").value(1100.00))
                .andExpect(jsonPath("$.customerDiscount").value(165.00))
                .andExpect(jsonPath("$.finalCustomerPrice").value(935.00));

        CleaningOrder referredOrder = orderRepository.findAll().getFirst();
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        AcquisitionSource.CUSTOMER_REFERRAL,
                        referredOrder.getAcquisitionSource()
                ),
                () -> Assertions.assertEquals(0L, rewardRepository.count())
        );

        complete(referredOrder);

        Assertions.assertEquals(
                1L,
                rewardRepository.countByCustomerIdAndStatus(
                        referrer.getId(),
                        ReferralRewardStatus.AVAILABLE
                )
        );
        Assertions.assertEquals(0L, payoutRepository.count());
    }

    @Test
    void partnerReferral_payoutBecomesPayableOnlyAfterCompleted() throws Exception {
        ReferralPartnerResponse partner = referralService.createPartner("Mahmutlar Realty");

        createOrder(800002L, partner.referralCode())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.basePrice").value(1100.00))
                .andExpect(jsonPath("$.customerDiscount").value(55.00))
                .andExpect(jsonPath("$.finalCustomerPrice").value(1045.00));

        CleaningOrder referredOrder = orderRepository.findAll().getFirst();
        Assertions.assertAll(
                () -> Assertions.assertEquals(AcquisitionSource.PARTNER, referredOrder.getAcquisitionSource()),
                () -> Assertions.assertEquals("110.00", referredOrder.getPartnerPayout().toPlainString()),
                () -> Assertions.assertEquals(0L, payoutRepository.count())
        );

        complete(referredOrder);

        PartnerPayout payout = payoutRepository.findAll().getFirst();
        Assertions.assertAll(
                () -> Assertions.assertEquals(PartnerPayoutStatus.PAYABLE, payout.getStatus()),
                () -> Assertions.assertEquals("110.00", payout.getAmount().toPlainString()),
                () -> Assertions.assertEquals(referredOrder.getId().longValue(), payout.getSourceOrderId())
        );
    }

    private org.springframework.test.web.servlet.ResultActions createOrder(
            long telegramUserId,
            String referralCode
    ) throws Exception {
        String userJson = """
                {"id":%d,"first_name":"Referral","last_name":"Customer","username":"customer%d"}
                """.formatted(telegramUserId, telegramUserId).strip();
        String initData = TelegramInitDataTestFactory.signed(BOT_TOKEN, Instant.now(), userJson);
        LocalDate requestedDate = LocalDate.now(ZoneId.of("Europe/Istanbul")).plusDays(1);
        String requestBody = """
                {
                  "area": "MAHMUTLAR",
                  "address": "Barbaros Cd. 24",
                  "apartmentType": "TWO_PLUS_ONE",
                  "duplex": false,
                  "cleaningType": "REGULAR",
                  "requestedDate": "%s",
                  "phone": "+90 555 123 45 67",
                  "comment": null,
                  "referralCode": "%s"
                }
                """.formatted(requestedDate, referralCode);

        return mvc.perform(post("/api/v1/cleaning/orders")
                .header("Authorization", "tma " + initData)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
    }

    private void complete(CleaningOrder order) {
        orderService.acceptOrder(order.getId(), CLEANER_ID);
        orderService.markAwaitingReport(order.getId(), CLEANER_ID);
        orderService.completeOrder(order.getId(), CLEANER_ID, null);
    }
}
