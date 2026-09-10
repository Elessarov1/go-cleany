package com.cleany.communication;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface OperationalTelegramDeliveryRepository
        extends JpaRepository<OperationalTelegramDelivery, Long> {

    @Query(value = """
            select * from operational_telegram_delivery delivery
             where delivery.expires_at > :now
               and ((delivery.status in ('PENDING', 'RETRY') and delivery.available_at <= :now)
                    or (delivery.status = 'PROCESSING' and delivery.lease_until < :now))
             order by delivery.available_at, delivery.id
             for update skip locked
             limit :batchSize
            """, nativeQuery = true)
    List<OperationalTelegramDelivery> claimable(@Param("now") Instant now,
                                                 @Param("batchSize") int batchSize);
}
