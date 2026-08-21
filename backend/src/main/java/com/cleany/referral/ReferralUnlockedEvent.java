package com.cleany.referral;

public record ReferralUnlockedEvent(
        long customerId,
        long communicationIdentityId,
        String referralCode
) {

    public ReferralUnlockedEvent {
        if (customerId <= 0) {
            throw new IllegalArgumentException("customerId must be positive");
        }
        if (communicationIdentityId <= 0) {
            throw new IllegalArgumentException("communicationIdentityId must be positive");
        }
        if (referralCode == null || referralCode.isBlank()) {
            throw new IllegalArgumentException("referralCode must not be blank");
        }
        referralCode = referralCode.trim();
    }
}
