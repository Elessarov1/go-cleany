package com.cleany.referral;

import com.cleany.finance.OrderFinancialSnapshot;

public record OrderReferralPlan(
        OrderFinancialSnapshot financialSnapshot,
        Long referralCodeId,
        Long referrerCustomerId,
        Long partnerId,
        Long rewardId
) {
}
