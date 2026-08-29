package com.cleany.transfer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferDriverRepository extends JpaRepository<TransferDriver, Long> {

    List<TransferDriver> findAllByOrderByNameAscIdAsc();

    List<TransferDriver> findAllByEnabledTrueOrderByNameAscIdAsc();

    Optional<TransferDriver> findByIdAndEnabledTrue(long id);

    Optional<TransferDriver> findByVerifiedTelegramUserIdAndEnabledTrue(long telegramUserId);

    List<TransferDriver> findAllByEnabledTrueAndVerifiedTelegramUserIdIsNotNullAndTelegramNotificationsEnabledTrueOrderByIdAsc();
}
