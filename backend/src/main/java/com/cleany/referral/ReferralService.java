package com.cleany.referral;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.finance.OrderFinancialCalculator;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderStatus;

@Service
public class ReferralService {

    private final ReferralCodeRepository codeRepository;
    private final ReferralPartnerRepository partnerRepository;
    private final ReferralRewardRepository rewardRepository;
    private final PartnerPayoutRepository payoutRepository;
    private final CleaningOrderRepository orderRepository;
    private final OrderFinancialCalculator financialCalculator;
    private final ReferralCodeGenerator codeGenerator;
    private final Clock clock;

    public ReferralService(
            ReferralCodeRepository codeRepository,
            ReferralPartnerRepository partnerRepository,
            ReferralRewardRepository rewardRepository,
            PartnerPayoutRepository payoutRepository,
            CleaningOrderRepository orderRepository,
            OrderFinancialCalculator financialCalculator,
            ReferralCodeGenerator codeGenerator,
            Clock clock
    ) {
        this.codeRepository = codeRepository;
        this.partnerRepository = partnerRepository;
        this.rewardRepository = rewardRepository;
        this.payoutRepository = payoutRepository;
        this.orderRepository = orderRepository;
        this.financialCalculator = financialCalculator;
        this.codeGenerator = codeGenerator;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public OrderReferralPlan quote(
            long customerId,
            String rawReferralCode,
            BigDecimal basePrice,
            boolean firstOrder
    ) {
        return plan(customerId, rawReferralCode, basePrice, firstOrder, false);
    }

    @Transactional
    public OrderReferralPlan planForCreation(
            long customerId,
            String rawReferralCode,
            BigDecimal basePrice,
            boolean firstOrder
    ) {
        return plan(customerId, rawReferralCode, basePrice, firstOrder, true);
    }

    @Transactional
    public void reserveReward(OrderReferralPlan plan, long orderId) {
        if (plan.rewardId() == null) {
            return;
        }
        ReferralReward reward = rewardRepository.findById(plan.rewardId())
                .orElseThrow(() -> new ReferralNotApplicableException("Referral reward not found"));
        reward.reserve(orderId);
    }

    @Transactional
    public void releaseReward(CleaningOrder order) {
        if (order.getAppliedRewardId() == null) {
            return;
        }
        rewardRepository.findById(order.getAppliedRewardId())
                .ifPresent(reward -> reward.release(order.getId()));
    }

    @Transactional
    public void completeOrder(CleaningOrder order) {
        if (order.getAppliedRewardId() != null) {
            ReferralReward reward = rewardRepository.findById(order.getAppliedRewardId())
                    .orElseThrow(() -> new ReferralNotApplicableException("Referral reward not found"));
            reward.redeem(order.getId(), order.getCompletedAt());
        }

        switch (order.getAcquisitionSource()) {
            case CUSTOMER_REFERRAL -> createReferrerReward(order);
            case PARTNER -> createPartnerPayout(order);
            case ORGANIC -> {
                // No acquisition-side reward.
            }
        }
        ensureCustomerReferralCode(order.getCustomerId());
    }

    @Transactional
    public ReferralSummaryResponse getSummary(long customerId) {
        boolean unlocked = orderRepository.existsByCustomerIdAndStatus(
                customerId,
                CleaningOrderStatus.COMPLETED
        );
        ReferralCode code = unlocked ? ensureCustomerReferralCode(customerId) : null;
        return new ReferralSummaryResponse(
                code == null ? null : code.getCode(),
                rewardRepository.countByCustomerIdAndStatus(customerId, ReferralRewardStatus.AVAILABLE),
                unlocked
        );
    }

    @Transactional
    public ReferralPartnerResponse createPartner(String rawName) {
        String name = rawName.trim();
        ReferralPartner partner = partnerRepository.save(new ReferralPartner(name, clock.instant()));
        ReferralCode code = createUniqueCode(value -> ReferralCode.partner(value, partner.getId(), clock.instant()));
        return partnerResponse(partner, code.getCode());
    }

    @Transactional(readOnly = true)
    public AdminReferralOverviewResponse getAdminOverview() {
        var partners = partnerRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(partner -> partnerResponse(
                        partner,
                        codeRepository.findByPartnerIdAndActiveTrue(partner.getId())
                                .map(ReferralCode::getCode)
                                .orElse("")
                ))
                .toList();
        var partnerNames = partners.stream().collect(java.util.stream.Collectors.toMap(
                ReferralPartnerResponse::id,
                ReferralPartnerResponse::name
        ));
        var payouts = payoutRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(payout -> payoutResponse(payout, partnerNames.getOrDefault(payout.getPartnerId(), "—")))
                .toList();
        return new AdminReferralOverviewResponse(partners, payouts);
    }

    @Transactional
    public PartnerPayoutResponse markPayoutPaid(long payoutId) {
        PartnerPayout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new ReferralNotApplicableException("Partner payout not found"));
        payout.markPaid(clock.instant());
        String partnerName = partnerRepository.findById(payout.getPartnerId())
                .map(ReferralPartner::getName)
                .orElse("—");
        return payoutResponse(payout, partnerName);
    }

    private OrderReferralPlan plan(
            long customerId,
            String rawReferralCode,
            BigDecimal basePrice,
            boolean firstOrder,
            boolean reserveCandidate
    ) {
        String referralCode = normalizeCode(rawReferralCode);
        if (referralCode != null) {
            if (!firstOrder) {
                throw new ReferralNotApplicableException(
                        "Referral code can only be applied to the customer's first order"
                );
            }
            ReferralCode code = codeRepository.findByCodeIgnoreCaseAndActiveTrue(referralCode)
                    .orElseThrow(() -> new ReferralNotApplicableException("Referral code is invalid"));
            return switch (code.getOwnerType()) {
                case CUSTOMER -> customerReferralPlan(customerId, basePrice, code);
                case PARTNER -> partnerReferralPlan(basePrice, code);
            };
        }

        Long rewardId = reserveCandidate
                ? rewardRepository.findFirstByCustomerIdAndStatusOrderByCreatedAtAsc(
                        customerId,
                        ReferralRewardStatus.AVAILABLE
                ).map(ReferralReward::getId).orElse(null)
                : null;
        boolean rewardAvailable = rewardId != null || (!reserveCandidate
                && rewardRepository.existsByCustomerIdAndStatus(
                        customerId,
                        ReferralRewardStatus.AVAILABLE
                ));
        if (rewardAvailable) {
            return new OrderReferralPlan(
                    financialCalculator.referrerReward(basePrice),
                    null,
                    null,
                    null,
                    rewardId
            );
        }
        return new OrderReferralPlan(financialCalculator.organic(basePrice), null, null, null, null);
    }

    private OrderReferralPlan customerReferralPlan(
            long customerId,
            BigDecimal basePrice,
            ReferralCode code
    ) {
        if (code.getCustomerId() == customerId) {
            throw new ReferralNotApplicableException("A customer cannot use their own referral code");
        }
        return new OrderReferralPlan(
                financialCalculator.customerReferral(basePrice),
                code.getId(),
                code.getCustomerId(),
                null,
                null
        );
    }

    private OrderReferralPlan partnerReferralPlan(BigDecimal basePrice, ReferralCode code) {
        ReferralPartner partner = partnerRepository.findById(code.getPartnerId())
                .filter(ReferralPartner::isActive)
                .orElseThrow(() -> new ReferralNotApplicableException("Referral partner is inactive"));
        return new OrderReferralPlan(
                financialCalculator.partnerReferral(basePrice),
                code.getId(),
                null,
                partner.getId(),
                null
        );
    }

    private void createReferrerReward(CleaningOrder order) {
        if (order.getReferrerCustomerId() == null || rewardRepository.existsBySourceOrderId(order.getId())) {
            return;
        }
        rewardRepository.save(new ReferralReward(
                order.getReferrerCustomerId(),
                order.getId(),
                order.getCompletedAt()
        ));
    }

    private void createPartnerPayout(CleaningOrder order) {
        if (order.getReferralPartnerId() == null || payoutRepository.existsBySourceOrderId(order.getId())) {
            return;
        }
        payoutRepository.save(new PartnerPayout(
                order.getReferralPartnerId(),
                order.getId(),
                order.getPartnerPayout(),
                order.getCurrency(),
                order.getCompletedAt()
        ));
    }

    private ReferralCode ensureCustomerReferralCode(long customerId) {
        return codeRepository.findByCustomerIdAndActiveTrue(customerId)
                .orElseGet(() -> createUniqueCode(
                        value -> ReferralCode.customer(value, customerId, clock.instant())
                ));
    }

    private ReferralCode createUniqueCode(java.util.function.Function<String, ReferralCode> factory) {
        for (int attempt = 0; attempt < 20; attempt++) {
            String value = codeGenerator.nextCode();
            if (!codeRepository.existsByCodeIgnoreCase(value)) {
                return codeRepository.save(factory.apply(value));
            }
        }
        throw new IllegalStateException("Unable to generate a unique referral code");
    }

    private static ReferralPartnerResponse partnerResponse(ReferralPartner partner, String code) {
        return new ReferralPartnerResponse(
                partner.getId(),
                partner.getName(),
                code,
                partner.isActive(),
                partner.getCreatedAt()
        );
    }

    private static PartnerPayoutResponse payoutResponse(PartnerPayout payout, String partnerName) {
        return new PartnerPayoutResponse(
                payout.getId(),
                payout.getPartnerId(),
                partnerName,
                payout.getSourceOrderId(),
                payout.getAmount(),
                payout.getCurrency(),
                payout.getStatus(),
                payout.getCreatedAt(),
                payout.getPaidAt()
        );
    }

    private static String normalizeCode(String value) {
        return value == null || value.isBlank() ? null : value.trim().toUpperCase(Locale.ROOT);
    }
}
