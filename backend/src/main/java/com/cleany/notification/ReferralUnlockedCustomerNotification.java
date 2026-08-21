package com.cleany.notification;

public record ReferralUnlockedCustomerNotification(String referralCode) implements CustomerNotification {

    public ReferralUnlockedCustomerNotification {
        if (referralCode == null || referralCode.isBlank()) {
            throw new IllegalArgumentException("referralCode must not be blank");
        }
        referralCode = referralCode.trim();
    }
}
