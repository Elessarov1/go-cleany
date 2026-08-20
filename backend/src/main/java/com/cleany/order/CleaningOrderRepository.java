package com.cleany.order;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CleaningOrderRepository extends JpaRepository<CleaningOrder, Long> {

    List<CleaningOrder> findAllByOrderByCreatedAtDesc();

    List<CleaningOrder> findAllByCustomerIdOrderByCreatedAtDesc(long customerId);

    Optional<CleaningOrder> findByIdAndCustomerId(long id, long customerId);

    boolean existsByCustomerIdAndStatus(long customerId, CleaningOrderStatus status);

    boolean existsByCustomerIdAndStatusIn(long customerId, List<CleaningOrderStatus> statuses);

    @Query(value = """
            select orders.id
              from cleaning_order orders
             where (
                       (orders.status = 'COMPLETED' and orders.completed_at < :cutoff)
                       or
                       (orders.status in ('CANCELLED', 'REJECTED') and (
                            select max(terminal_event.occurred_at)
                              from cleaning_order_event terminal_event
                             where terminal_event.order_id = orders.id
                               and terminal_event.to_status = orders.status
                       ) < :cutoff)
                   )
               and (
                       exists (
                           select 1
                             from cleaning_order_event audit_event
                            where audit_event.order_id = orders.id
                       )
                       or exists (
                           select 1
                             from cleaning_order_photo completion_photo
                            where completion_photo.order_id = orders.id
                       )
                       or exists (
                           select 1
                             from cleaning_order_issue_report issue_report
                             join cleaning_order_issue_photo issue_photo
                               on issue_photo.issue_report_id = issue_report.id
                            where issue_report.order_id = orders.id
                              and issue_report.resolved_at is not null
                       )
                   )
             order by orders.id
            """, nativeQuery = true)
    List<Long> findRetentionEligibleOrderIds(@Param("cutoff") Instant cutoff);

    Optional<CleaningOrder> findByCleanerTelegramUserIdAndReportInputActiveTrue(long cleanerTelegramUserId);

    @Modifying(flushAutomatically = true)
    @Query("""
            update CleaningOrder order
               set order.reportInputActive = false
             where order.cleanerTelegramUserId = :cleanerId
               and order.id <> :activeOrderId
               and order.reportInputActive = true
            """)
    int deactivateOtherReportInputs(
            @Param("cleanerId") long cleanerId,
            @Param("activeOrderId") long activeOrderId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update CleaningOrder order
               set order.status = :acceptedStatus,
                   order.cleanerTelegramUserId = :cleanerId,
                   order.acceptedAt = :acceptedAt
             where order.id = :orderId
               and order.status = :newStatus
            """)
    int claimNewOrder(
            @Param("orderId") long orderId,
            @Param("cleanerId") long cleanerId,
            @Param("acceptedAt") Instant acceptedAt,
            @Param("newStatus") CleaningOrderStatus newStatus,
            @Param("acceptedStatus") CleaningOrderStatus acceptedStatus
    );
}
