package com.cleany.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CleaningOrderIssuePhotoRepository extends JpaRepository<CleaningOrderIssuePhoto, Long> {

    List<CleaningOrderIssuePhoto> findAllByIssueReport_IdOrderByCreatedAtAscIdAsc(long issueReportId);

    @Query("""
            select new com.cleany.order.CleaningOrderIssuePhotoMetadata(
                photo.id,
                photo.contentType,
                photo.sizeBytes,
                photo.sha256,
                photo.createdAt
            )
              from CleaningOrderIssuePhoto photo
             where photo.issueReport.id = :issueReportId
             order by photo.createdAt asc, photo.id asc
            """)
    List<CleaningOrderIssuePhotoMetadata> findMetadataByIssueReportId(
            @Param("issueReportId") long issueReportId
    );

    @Query("""
            select photo.telegramFileId
              from CleaningOrderIssuePhoto photo
             where photo.issueReport.id = :issueReportId
             order by photo.createdAt asc, photo.id asc
            """)
    List<String> findTelegramFileIdsByIssueReportId(@Param("issueReportId") long issueReportId);

    Optional<CleaningOrderIssuePhoto> findByIdAndIssueReport_Order_IdAndIssueReport_SubmittedAtIsNotNull(
            long id,
            long orderId
    );

    boolean existsByIssueReport_IdAndTelegramFileUniqueId(long issueReportId, String telegramFileUniqueId);

    long countByIssueReport_Id(long issueReportId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from CleaningOrderIssuePhoto photo
             where photo.issueReport.id in (
                 select report.id
                   from CleaningOrderIssueReport report
                  where report.order.id in :orderIds
                    and report.resolvedAt is not null
             )
            """)
    int deleteResolvedByOrderIds(@Param("orderIds") List<Long> orderIds);
}
