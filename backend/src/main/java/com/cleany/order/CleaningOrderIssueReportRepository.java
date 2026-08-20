package com.cleany.order;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CleaningOrderIssueReportRepository
        extends JpaRepository<CleaningOrderIssueReport, Long> {

    Optional<CleaningOrderIssueReport> findByOrder_Id(long orderId);

    Optional<CleaningOrderIssueReport> findByOrder_IdAndSubmittedAtIsNotNull(long orderId);

    Optional<CleaningOrderIssueReport>
    findByCleanerTelegramUserIdAndInputActiveTrueAndSubmittedAtIsNullAndOrder_Status(
            long cleanerTelegramUserId,
            CleaningOrderStatus status
    );

    @Modifying(flushAutomatically = true)
    @Query("""
            update CleaningOrderIssueReport report
               set report.inputActive = false
             where report.cleanerTelegramUserId = :cleanerId
               and report.order.id <> :activeOrderId
               and report.inputActive = true
               and report.submittedAt is null
            """)
    int deactivateOtherDrafts(
            @Param("cleanerId") long cleanerId,
            @Param("activeOrderId") long activeOrderId
    );
}
