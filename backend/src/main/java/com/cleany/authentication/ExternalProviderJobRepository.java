package com.cleany.authentication;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface ExternalProviderJobRepository extends JpaRepository<ExternalProviderJob, Long> {
    @Query(value = """
            select * from external_provider_job job
             where (job.status in ('PENDING', 'RETRY') and job.available_at <= :now)
                or (job.status = 'PROCESSING' and job.lease_until < :now)
             order by job.available_at, job.id for update skip locked limit :batchSize
            """, nativeQuery = true)
    List<ExternalProviderJob> claimable(@Param("now") Instant now, @Param("batchSize") int batchSize);
}
