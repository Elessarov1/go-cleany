package com.cleany.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.referral.ReferralUnlockedEvent;

@Component
public class ReferralUnlockedNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(ReferralUnlockedNotificationListener.class);

    private final CustomerNotificationDispatcher dispatcher;

    public ReferralUnlockedNotificationListener(CustomerNotificationDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Order(10)
    public void notifyCustomer(ReferralUnlockedEvent event) {
        try {
            dispatcher.send(
                    event.customerId(),
                    event.communicationIdentityId(),
                    new ReferralUnlockedCustomerNotification(event.referralCode())
            );
        } catch (RuntimeException exception) {
            log.error(
                    "Referral unlock notification failed for customer {} and communication identity {}",
                    event.customerId(),
                    event.communicationIdentityId(),
                    exception
            );
        }
    }
}
