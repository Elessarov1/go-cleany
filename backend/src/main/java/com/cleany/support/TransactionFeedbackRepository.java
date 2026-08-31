package com.cleany.support;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cleany.catalog.PlatformService;

public interface TransactionFeedbackRepository extends JpaRepository<TransactionFeedback, Long> {

    Optional<TransactionFeedback> findByCustomerIdAndServiceAndSourceEntityId(
            long customerId,
            PlatformService service,
            long sourceEntityId
    );
}
