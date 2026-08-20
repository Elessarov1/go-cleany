package com.cleany.notification;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.referral.ReferralUnlockedEvent;

@Component
public class ReferralUnlockedNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(ReferralUnlockedNotificationListener.class);

    private final List<CustomerNotificationSender> senders;

    public ReferralUnlockedNotificationListener(List<CustomerNotificationSender> senders) {
        this.senders = List.copyOf(senders);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyCustomer(ReferralUnlockedEvent event) {
        for (CustomerNotificationSender sender : senders) {
            try {
                sender.sendReferralUnlocked(event.customerId(), event.referralCode());
            } catch (RuntimeException exception) {
                log.error(
                        "Referral unlock notification failed for customer {} through {}",
                        event.customerId(),
                        sender.getClass().getSimpleName(),
                        exception
                );
            }
        }
    }
}
