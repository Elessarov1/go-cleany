package com.cleany.crossservice.rentalcleaning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.notification.CustomerNotificationDispatcher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RentalCleaningBenefitNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(
            RentalCleaningBenefitNotificationListener.class
    );

    private final CustomerNotificationDispatcher dispatcher;
    private final RentalCleaningBenefitNotificationQueryService queryService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Order(10)
    public void notifyCustomer(RentalCleaningBenefitIssuedEvent event) {
        try {
            dispatcher.send(
                    event.customerId(),
                    event.communicationIdentityId(),
                    queryService.issued(event.benefitId())
            );
        } catch (RuntimeException exception) {
            log.error(
                    "Rental cleaning benefit notification failed: benefitId={}, bookingId={}, "
                            + "communicationIdentityId={}",
                    event.benefitId(),
                    event.rentalBookingId(),
                    event.communicationIdentityId(),
                    exception
            );
        }
    }
}
