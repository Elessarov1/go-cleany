package com.cleany.referral;

import java.time.Instant;

public record ReferralPartnerResponse(
        long id,
        String name,
        String referralCode,
        boolean active,
        Instant createdAt
) {
}
