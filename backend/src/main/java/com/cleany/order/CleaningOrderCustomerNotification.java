package com.cleany.order;

import java.time.LocalDate;
import java.util.List;

import com.cleany.notification.CustomerNotification;
import com.cleany.notification.CustomerNotificationType;

public sealed interface CleaningOrderCustomerNotification extends CustomerNotification {

    long orderId();

    @Override
    default CustomerNotificationType type() {
        return switch (this) {
            case Accepted ignored -> CustomerNotificationType.CLEANING_ORDER_ACCEPTED;
            case Cancelled ignored -> CustomerNotificationType.CLEANING_ORDER_CANCELLED;
            case Completed ignored -> CustomerNotificationType.CLEANING_ORDER_COMPLETED;
            case OnsiteIssueReported ignored -> CustomerNotificationType.CLEANING_ONSITE_ISSUE_REPORTED;
        };
    }

    @Override
    default String targetPath() {
        return "/cleaning/orders/" + orderId();
    }

    @Override
    default String deduplicationKey() {
        return "cleaning-order:" + orderId() + ":" + switch (this) {
            case Accepted ignored -> "accepted";
            case Cancelled ignored -> "cancelled";
            case Completed ignored -> "completed";
            case OnsiteIssueReported ignored -> "onsite-issue";
        };
    }

    record Accepted(long orderId) implements CleaningOrderCustomerNotification {
    }

    record Cancelled(long orderId) implements CleaningOrderCustomerNotification {
    }

    record Completed(
            long orderId,
            ApartmentType apartmentType,
            boolean duplex,
            ServiceArea area,
            LocalDate requestedDate,
            String cleanerComment,
            List<Long> mediaIds,
            int reportRetentionDays
    ) implements CleaningOrderCustomerNotification {

        public Completed {
            mediaIds = List.copyOf(mediaIds);
            if (reportRetentionDays < 1) {
                throw new IllegalArgumentException("reportRetentionDays must be positive");
            }
        }

    }

    record OnsiteIssueReported(
            long orderId,
            OnsiteIssueReason reason,
            String comment,
            List<Long> mediaIds
    ) implements CleaningOrderCustomerNotification {

        public OnsiteIssueReported {
            mediaIds = List.copyOf(mediaIds);
        }
    }
}
