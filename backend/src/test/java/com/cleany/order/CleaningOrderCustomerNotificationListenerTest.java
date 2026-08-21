package com.cleany.order;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.notification.CustomerNotificationDispatcher;

class CleaningOrderCustomerNotificationListenerTest {

    private final CustomerNotificationDispatcher dispatcher =
            Mockito.mock(CustomerNotificationDispatcher.class);
    private final CleaningOrderCustomerNotificationQueryService queryService =
            Mockito.mock(CleaningOrderCustomerNotificationQueryService.class);
    private final OnsiteIssueService onsiteIssueService = Mockito.mock(OnsiteIssueService.class);
    private final CleaningOrderCustomerNotificationListener listener =
            new CleaningOrderCustomerNotificationListener(dispatcher, queryService, onsiteIssueService);

    @Test
    void listener_runsOnlyAfterSuccessfulCommit() throws NoSuchMethodException {
        var method = CleaningOrderCustomerNotificationListener.class.getDeclaredMethod(
                "notifyCustomer",
                CleaningOrderCustomerEvent.class
        );
        var annotation = method.getAnnotation(TransactionalEventListener.class);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(annotation),
                () -> Assertions.assertEquals(TransactionPhase.AFTER_COMMIT, annotation.phase()),
                () -> Assertions.assertFalse(annotation.fallbackExecution())
        );
    }

    @Test
    void acceptedOrder_dispatchedThroughRecordedCommunicationIdentity() {
        var event = new CleaningOrderCustomerEvent.Accepted(43L, 77L, 88L);

        listener.notifyCustomer(event);

        Mockito.verify(dispatcher).send(
                77L,
                88L,
                new CleaningOrderCustomerNotification.Accepted(43L)
        );
        Mockito.verifyNoInteractions(queryService, onsiteIssueService);
    }

    @Test
    void submittedOnsiteIssue_successfulDeliveryRecordedAfterDispatch() {
        var event = new CleaningOrderCustomerEvent.OnsiteIssueReported(43L, 77L, 88L, 101L);
        var notification = new CleaningOrderCustomerNotification.OnsiteIssueReported(
                43L,
                OnsiteIssueReason.ACCESS_PROBLEM,
                "Нет ключа",
                List.of()
        );
        Mockito.when(queryService.onsiteIssue(43L)).thenReturn(notification);
        Mockito.when(dispatcher.send(77L, 88L, notification)).thenReturn(true);

        listener.notifyCustomer(event);

        var order = Mockito.inOrder(dispatcher, onsiteIssueService);
        order.verify(dispatcher).send(77L, 88L, notification);
        order.verify(onsiteIssueService).recordCustomerNotified(43L, 101L);
    }

    @Test
    void failedDelivery_doesNotRecordOnsiteAuditOrEscapeAfterCommitListener() {
        var event = new CleaningOrderCustomerEvent.OnsiteIssueReported(43L, 77L, 88L, 101L);
        var notification = new CleaningOrderCustomerNotification.OnsiteIssueReported(
                43L,
                OnsiteIssueReason.ACCESS_PROBLEM,
                "Нет ключа",
                List.of()
        );
        Mockito.when(queryService.onsiteIssue(43L)).thenReturn(notification);
        Mockito.when(dispatcher.send(77L, 88L, notification))
                .thenThrow(new IllegalStateException("channel unavailable"));

        Assertions.assertDoesNotThrow(() -> listener.notifyCustomer(event));

        Mockito.verify(onsiteIssueService, Mockito.never()).recordCustomerNotified(
                Mockito.anyLong(),
                Mockito.anyLong()
        );
    }

    @Test
    void unavailableChannel_doesNotRecordOnsiteDeliveryAudit() {
        var event = new CleaningOrderCustomerEvent.OnsiteIssueReported(43L, 77L, 88L, 101L);
        var notification = new CleaningOrderCustomerNotification.OnsiteIssueReported(
                43L,
                OnsiteIssueReason.ACCESS_PROBLEM,
                "Нет ключа",
                List.of()
        );
        Mockito.when(queryService.onsiteIssue(43L)).thenReturn(notification);
        Mockito.when(dispatcher.send(77L, 88L, notification)).thenReturn(false);

        listener.notifyCustomer(event);

        Mockito.verify(onsiteIssueService, Mockito.never()).recordCustomerNotified(
                Mockito.anyLong(),
                Mockito.anyLong()
        );
    }
}
