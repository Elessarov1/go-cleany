package com.cleany.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CleaningOrderEventRepository extends JpaRepository<CleaningOrderEvent, Long> {

    List<CleaningOrderEvent> findAllByOrderIdOrderByOccurredAtAscIdAsc(long orderId);
}
