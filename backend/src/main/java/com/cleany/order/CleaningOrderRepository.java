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
