package com.cleany.order;

public sealed interface CleaningOrderCustomerEvent {

    long orderId();

    long customerId();

    long communicationIdentityId();

    record Accepted(
            long orderId,
            long customerId,
            long communicationIdentityId
    ) implements CleaningOrderCustomerEvent {
    }

    record Cancelled(
            long orderId,
            long customerId,
            long communicationIdentityId
    ) implements CleaningOrderCustomerEvent {
    }

    record Completed(
            long orderId,
            long customerId,
            long communicationIdentityId
    ) implements CleaningOrderCustomerEvent {
    }

    record OnsiteIssueReported(
            long orderId,
            long customerId,
            long communicationIdentityId,
            long cleanerTelegramUserId
    ) implements CleaningOrderCustomerEvent {
    }
}
