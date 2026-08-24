package com.cleany.finance;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

@Service
public class OrderFinancialCalculator {

    private static final int MONEY_SCALE = 2;

    private final ReferralFinancialProperties properties;

    public OrderFinancialCalculator(ReferralFinancialProperties properties) {
        this.properties = properties;
        validateConfiguration();
    }

    public OrderFinancialSnapshot organic(BigDecimal basePrice) {
        return calculate(
                basePrice,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                AcquisitionSource.ORGANIC,
                CustomerDiscountType.NONE
        );
    }

    public OrderFinancialSnapshot customerReferral(BigDecimal basePrice) {
        return calculate(
                basePrice,
                properties.customer().friendDiscountRate(),
                properties.customer().friendMaxDiscount(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                AcquisitionSource.CUSTOMER_REFERRAL,
                CustomerDiscountType.FRIEND_REFERRAL
        );
    }

    public OrderFinancialSnapshot referrerReward(BigDecimal basePrice) {
        return calculate(
                basePrice,
                properties.customer().referrerRewardRate(),
                properties.customer().referrerMaxDiscount(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                AcquisitionSource.ORGANIC,
                CustomerDiscountType.REFERRER_REWARD
        );
    }

    public OrderFinancialSnapshot partnerReferral(BigDecimal basePrice) {
        return calculate(
                basePrice,
                properties.partner().customerDiscountRate(),
                properties.partner().customerMaxDiscount(),
                properties.partner().payoutRate(),
                properties.partner().maxPayout(),
                AcquisitionSource.PARTNER,
                CustomerDiscountType.PARTNER_REFERRAL
        );
    }

    public OrderFinancialSnapshot rentalCheckoutPromo(
            BigDecimal basePrice,
            BigDecimal discountRate,
            BigDecimal maxDiscount
    ) {
        validateRentalCheckoutPromoRate(discountRate);
        return calculate(
                basePrice,
                discountRate,
                maxDiscount,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                AcquisitionSource.ORGANIC,
                CustomerDiscountType.RENTAL_CHECKOUT_PROMO
        );
    }

    public void validateRentalCheckoutPromoRate(BigDecimal discountRate) {
        requireWithinPool(
                "rental checkout discount",
                discountRate,
                properties.commissionRate()
        );
    }

    private OrderFinancialSnapshot calculate(
            BigDecimal rawBasePrice,
            BigDecimal discountRate,
            BigDecimal discountCap,
            BigDecimal payoutRate,
            BigDecimal payoutCap,
            AcquisitionSource acquisitionSource,
            CustomerDiscountType discountType
    ) {
        BigDecimal basePrice = money(rawBasePrice);
        BigDecimal commissionRate = properties.commissionRate();
        BigDecimal baseCommission = percentage(basePrice, commissionRate);
        BigDecimal customerDiscount = min(
                percentage(basePrice, discountRate),
                money(discountCap),
                baseCommission
        );
        BigDecimal remainingBudget = baseCommission.subtract(customerDiscount);
        BigDecimal partnerPayout = min(
                percentage(basePrice, payoutRate),
                money(payoutCap),
                remainingBudget
        );
        BigDecimal totalIncentives = customerDiscount.add(partnerPayout);
        if (totalIncentives.compareTo(baseCommission) > 0) {
            throw new InvalidFinancialConfigurationException(
                    "Service-funded incentives exceed the order commission pool"
            );
        }

        return new OrderFinancialSnapshot(
                basePrice,
                commissionRate,
                baseCommission,
                customerDiscount,
                partnerPayout,
                basePrice.subtract(customerDiscount),
                baseCommission.subtract(totalIncentives),
                acquisitionSource,
                discountType
        );
    }

    private void validateConfiguration() {
        BigDecimal commissionRate = properties.commissionRate();
        requireWithinPool(
                "customer referral discount",
                properties.customer().friendDiscountRate(),
                commissionRate
        );
        requireWithinPool(
                "referrer reward",
                properties.customer().referrerRewardRate(),
                commissionRate
        );
        BigDecimal partnerTotal = properties.partner().customerDiscountRate()
                .add(properties.partner().payoutRate());
        requireWithinPool("partner acquisition incentives", partnerTotal, commissionRate);
    }

    private static void requireWithinPool(String incentive, BigDecimal rate, BigDecimal commissionRate) {
        if (rate.compareTo(commissionRate) > 0) {
            throw new InvalidFinancialConfigurationException(
                    incentive + " rate " + rate + " exceeds commission rate " + commissionRate
            );
        }
    }

    private static BigDecimal percentage(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private static BigDecimal money(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new InvalidFinancialConfigurationException("Financial amount must be non-negative");
        }
        return amount.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private static BigDecimal min(BigDecimal first, BigDecimal second, BigDecimal third) {
        return first.min(second).min(third);
    }
}
