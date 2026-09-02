package com.cleany.reminder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cleany.catalog.PlatformService;

public interface CustomerReminderRepository extends JpaRepository<CustomerReminder, Long> {

    Optional<CustomerReminder> findByCustomerIdAndTypeAndSourceServiceAndSourceEntityId(
            long customerId,
            CustomerReminderType type,
            PlatformService sourceService,
            long sourceEntityId
    );

    List<CustomerReminder> findAllByTypeAndStatusAndScheduledDateLessThanEqualOrderByScheduledDateAscIdAsc(
            CustomerReminderType type,
            CustomerReminderStatus status,
            LocalDate date,
            Pageable pageable
    );
}
