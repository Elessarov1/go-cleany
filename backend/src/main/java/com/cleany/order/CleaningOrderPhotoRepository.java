package com.cleany.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CleaningOrderPhotoRepository extends JpaRepository<CleaningOrderPhoto, Long> {

    List<CleaningOrderPhoto> findAllByOrderIdOrderByCreatedAt(long orderId);

    boolean existsByOrderIdAndTelegramFileUniqueId(long orderId, String telegramFileUniqueId);

    long countByOrderId(long orderId);
}
