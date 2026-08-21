package com.cleany.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.notification.CustomerNotification;
import com.cleany.notification.CustomerNotificationDispatcher;

@Component
public class CleaningOrderCustomerNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(
            CleaningOrderCustomerNotificationListener.class
    );

    private final CustomerNotificationDispatcher dispatcher;
    private final CleaningOrderCustomerNotificationQueryService notificationQueryService;
    private final OnsiteIssueService onsiteIssueService;

    public CleaningOrderCustomerNotificationListener(
            CustomerNotificationDispatcher dispatcher,
            CleaningOrderCustomerNotificationQueryService notificationQueryService,
            OnsiteIssueService onsiteIssueService
    ) {
        this.dispatcher = dispatcher;
        this.notificationQueryService = notificationQueryService;
        this.onsiteIssueService = onsiteIssueService;
    }

    @Order(0)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyCustomer(CleaningOrderCustomerEvent event) {
        try {
            CustomerNotification notification = notification(event);
            boolean delivered = dispatcher.send(
                    event.customerId(),
                    event.communicationIdentityId(),
                    notification
            );
            if (delivered && event instanceof CleaningOrderCustomerEvent.OnsiteIssueReported issue) {
                onsiteIssueService.recordCustomerNotified(
                        issue.orderId(),
                        issue.cleanerTelegramUserId()
                );
            }
        } catch (RuntimeException exception) {
            log.error(
                    "Cleaning order customer notification failed for order {} and communication identity {}",
                    event.orderId(),
                    event.communicationIdentityId(),
                    exception
            );
        }
    }

    private CustomerNotification notification(CleaningOrderCustomerEvent event) {
        return switch (event) {
            case CleaningOrderCustomerEvent.Accepted accepted ->
                    new CleaningOrderCustomerNotification.Accepted(accepted.orderId());
            case CleaningOrderCustomerEvent.Cancelled cancelled ->
                    new CleaningOrderCustomerNotification.Cancelled(cancelled.orderId());
            case CleaningOrderCustomerEvent.Completed completed ->
                    notificationQueryService.completed(completed.orderId());
            case CleaningOrderCustomerEvent.OnsiteIssueReported issue ->
                    notificationQueryService.onsiteIssue(issue.orderId());
        };
    }
}
