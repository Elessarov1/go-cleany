package com.cleany.referral;

import java.util.List;

public record AdminReferralOverviewResponse(
        List<ReferralPartnerResponse> partners,
        List<PartnerPayoutResponse> payouts
) {
}
