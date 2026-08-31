package com.cleany.support;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cleany.catalog.PlatformService;

public interface SupportCaseRepository extends JpaRepository<SupportCase, Long> {

    Optional<SupportCase> findFirstByCustomerIdAndServiceAndSourceEntityIdOrderByCreatedAtDescIdDesc(
            long customerId,
            PlatformService service,
            long sourceEntityId
    );

    Optional<SupportCase> findByCustomerIdAndServiceAndSourceEntityIdAndStatus(
            long customerId,
            PlatformService service,
            long sourceEntityId,
            SupportCaseStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select supportCase from SupportCase supportCase where supportCase.id = :id")
    Optional<SupportCase> findByIdForUpdate(@Param("id") long id);

    @Query("""
            select supportCase from SupportCase supportCase
             where (:status is null or supportCase.status = :status)
               and (:service is null or supportCase.service = :service)
             order by case when supportCase.status = com.cleany.support.SupportCaseStatus.OPEN then 0 else 1 end,
                      case when supportCase.status = com.cleany.support.SupportCaseStatus.OPEN then supportCase.createdAt end asc,
                      case when supportCase.status = com.cleany.support.SupportCaseStatus.RESOLVED then supportCase.resolvedAt end desc,
                      supportCase.id desc
            """)
    Page<SupportCase> findQueue(
            @Param("status") SupportCaseStatus status,
            @Param("service") PlatformService service,
            Pageable pageable
    );
}
