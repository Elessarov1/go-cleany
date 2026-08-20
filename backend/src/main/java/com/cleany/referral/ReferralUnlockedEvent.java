package com.cleany.referral;

import java.util.Objects;

public record ReferralUnlockedEvent(long customerId, String referralCode) {

    public ReferralUnlockedEvent {
        if (customerId <= 0) {
            throw new IllegalArgumentException("customerId must be positive");
        }
        referralCode = Objects.requireNonNull(referralCode);
        if (referralCode.isBlank()) {
            throw new IllegalArgumentException("referralCode must not be blank");
        }
    }
}
