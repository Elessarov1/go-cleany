package com.cleany.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CleaningOrderPhotoRepository extends JpaRepository<CleaningOrderPhoto, Long> {

    List<CleaningOrderPhoto> findAllByOrderIdOrderByCreatedAt(long orderId);

    boolean existsByOrderIdAndTelegramFileUniqueId(long orderId, String telegramFileUniqueId);

    long countByOrderId(long orderId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from CleaningOrderPhoto photo where photo.order.id in :orderIds")
    int deleteByOrderIds(@Param("orderIds") List<Long> orderIds);
}
