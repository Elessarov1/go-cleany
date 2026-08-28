package com.cleany.notification;

public record ReferralUnlockedCustomerNotification(String referralCode) implements CustomerNotification {

    public ReferralUnlockedCustomerNotification {
        if (referralCode == null || referralCode.isBlank()) {
            throw new IllegalArgumentException("referralCode must not be blank");
        }
        referralCode = referralCode.trim();
    }

    @Override
    public CustomerNotificationType type() {
        return CustomerNotificationType.REFERRAL_UNLOCKED;
    }

    @Override
    public String targetPath() {
        return "/cleaning/orders";
    }

    @Override
    public String deduplicationKey() {
        return "referral:" + referralCode + ":unlocked";
    }
}
