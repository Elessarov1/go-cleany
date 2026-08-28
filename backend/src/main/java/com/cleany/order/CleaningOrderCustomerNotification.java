package com.cleany.order;

import java.time.LocalDate;
import java.util.List;

import com.cleany.notification.CustomerNotification;

public sealed interface CleaningOrderCustomerNotification extends CustomerNotification {

    long orderId();

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

        public Completed(
                long orderId,
                ApartmentType apartmentType,
                boolean duplex,
                ServiceArea area,
                LocalDate requestedDate,
                String cleanerComment,
                List<Long> mediaIds
        ) {
            this(orderId, apartmentType, duplex, area, requestedDate, cleanerComment, mediaIds, 7);
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
