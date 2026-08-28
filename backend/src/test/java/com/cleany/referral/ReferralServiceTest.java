package com.cleany.referral;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.cleany.finance.AcquisitionSource;
import com.cleany.finance.OrderFinancialCalculator;
import com.cleany.finance.ReferralFinancialProperties;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderRepository;

class ReferralServiceTest {

    private static final Instant NOW = Instant.parse("2026-08-19T12:00:00Z");

    private ReferralCodeRepository codeRepository;
    private ReferralPartnerRepository partnerRepository;
    private ReferralRewardRepository rewardRepository;
    private PartnerPayoutRepository payoutRepository;
    private CleaningOrderRepository orderRepository;
    private ReferralService service;

    @BeforeEach
    void setUp() {
        codeRepository = Mockito.mock(ReferralCodeRepository.class);
        partnerRepository = Mockito.mock(ReferralPartnerRepository.class);
        rewardRepository = Mockito.mock(ReferralRewardRepository.class);
        payoutRepository = Mockito.mock(PartnerPayoutRepository.class);
        orderRepository = Mockito.mock(CleaningOrderRepository.class);
        service = new ReferralService(
                codeRepository,
                partnerRepository,
                rewardRepository,
                payoutRepository,
                orderRepository,
                new OrderFinancialCalculator(properties()),
                Mockito.mock(ReferralCodeGenerator.class),
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
    }

    @Test
    void firstOrder_customerReferral_friendDiscountPlanned() {
        ReferralCode code = Mockito.mock(ReferralCode.class);
        Mockito.when(code.getOwnerType()).thenReturn(ReferralCodeOwnerType.CUSTOMER);
        Mockito.when(code.getCustomerId()).thenReturn(10L);
        Mockito.when(code.getId()).thenReturn(20L);
        Mockito.when(codeRepository.findByCodeIgnoreCaseAndActiveTrue("FRIEND10"))
                .thenReturn(Optional.of(code));

        OrderReferralPlan plan = service.planForCreation(11L, " friend10 ", amount("1000"), true);

        Assertions.assertAll(
                () -> Assertions.assertEquals(AcquisitionSource.CUSTOMER_REFERRAL,
                        plan.financialSnapshot().acquisitionSource()),
                () -> assertAmount("150.00", plan.financialSnapshot().customerDiscount()),
                () -> assertAmount("850.00", plan.financialSnapshot().finalCustomerPrice()),
                () -> Assertions.assertEquals(10L, plan.referrerCustomerId())
        );
    }

    @Test
    void existingCustomer_referralCodeRejected() {
        Assertions.assertThrows(
                ReferralNotApplicableException.class,
                () -> service.planForCreation(11L, "FRIEND10", amount("1000"), false)
        );
        Mockito.verifyNoInteractions(codeRepository);
    }

    @Test
    void availableRewards_onlyOneRewardReservedForOrder() {
        ReferralReward reward = Mockito.mock(ReferralReward.class);
        Mockito.when(reward.getId()).thenReturn(42L);
        Mockito.when(rewardRepository.findFirstByCustomerIdAndStatusOrderByCreatedAtAsc(
                11L,
                ReferralRewardStatus.AVAILABLE
        )).thenReturn(Optional.of(reward));

        OrderReferralPlan plan = service.planForCreation(11L, null, amount("1000"), false);

        Assertions.assertAll(
                () -> Assertions.assertEquals(42L, plan.rewardId()),
                () -> assertAmount("100.00", plan.financialSnapshot().customerDiscount()),
                () -> assertAmount("50.00", plan.financialSnapshot().platformNet())
        );
    }

    @Test
    void completedCustomerReferral_rewardCreatedForReferrerOnlyAfterCompletion() {
        CleaningOrder order = completedOrder(AcquisitionSource.CUSTOMER_REFERRAL);
        Mockito.when(order.getReferrerCustomerId()).thenReturn(10L);
        Mockito.when(codeRepository.findFirstByCustomerIdAndActiveTrueOrderByCreatedAtAsc(11L))
                .thenReturn(Optional.of(Mockito.mock(ReferralCode.class)));

        service.completeOrder(order);

        ArgumentCaptor<ReferralReward> reward = ArgumentCaptor.forClass(ReferralReward.class);
        Mockito.verify(rewardRepository).save(reward.capture());
        Mockito.verifyNoInteractions(payoutRepository);
    }

    @Test
    void completedPartnerAcquisition_payablePayoutCreatedOnce() {
        CleaningOrder order = completedOrder(AcquisitionSource.PARTNER);
        Mockito.when(order.getReferralPartnerId()).thenReturn(30L);
        Mockito.when(order.getPartnerPayout()).thenReturn(amount("100"));
        Mockito.when(order.getCurrency()).thenReturn("TRY");
        Mockito.when(codeRepository.findFirstByCustomerIdAndActiveTrueOrderByCreatedAtAsc(11L))
                .thenReturn(Optional.of(Mockito.mock(ReferralCode.class)));

        service.completeOrder(order);

        ArgumentCaptor<PartnerPayout> payout = ArgumentCaptor.forClass(PartnerPayout.class);
        Mockito.verify(payoutRepository).save(payout.capture());
        Assertions.assertAll(
                () -> assertAmount("100.00", payout.getValue().getAmount()),
                () -> Assertions.assertEquals(PartnerPayoutStatus.PAYABLE, payout.getValue().getStatus())
        );
        Mockito.verifyNoInteractions(rewardRepository);
    }

    private CleaningOrder completedOrder(AcquisitionSource source) {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(order.getId()).thenReturn(100L);
        Mockito.when(order.getCustomerId()).thenReturn(11L);
        Mockito.when(order.getAcquisitionSource()).thenReturn(source);
        Mockito.when(order.getCompletedAt()).thenReturn(NOW);
        Mockito.when(order.getAppliedRewardId()).thenReturn(null);
        return order;
    }

    private static ReferralFinancialProperties properties() {
        return new ReferralFinancialProperties(
                amount("0.15"),
                new ReferralFinancialProperties.Customer(
                        amount("0.15"), amount("2000"), amount("0.10"), amount("2000")
                ),
                new ReferralFinancialProperties.Partner(
                        amount("0.05"), amount("2000"), amount("0.10"), amount("2000")
                )
        );
    }

    private static BigDecimal amount(String value) {
        return new BigDecimal(value);
    }

    private static void assertAmount(String expected, BigDecimal actual) {
        Assertions.assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
