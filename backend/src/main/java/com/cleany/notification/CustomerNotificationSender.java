package com.cleany.notification;

public interface CustomerNotificationSender {

    void sendReferralUnlocked(long customerId, String referralCode);
}
