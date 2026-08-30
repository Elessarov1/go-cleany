package com.cleany.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CleaningOrderEventRepository extends JpaRepository<CleaningOrderEvent, Long> {

    List<CleaningOrderEvent> findAllByOrderIdOrderByOccurredAtAscIdAsc(long orderId);

    List<CleaningOrderEvent> findAllByOrder_IdInOrderByOccurredAtAscIdAsc(List<Long> orderIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from CleaningOrderEvent orderEvent where orderEvent.order.id in :orderIds")
    int deleteByOrderIds(@Param("orderIds") List<Long> orderIds);
}
