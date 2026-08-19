package com.cleany.referral;

public record ReferralSummaryResponse(
        String referralCode,
        long availableRewards,
        boolean referralProgramUnlocked
) {
}
