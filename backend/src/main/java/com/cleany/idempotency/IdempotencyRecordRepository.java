package com.cleany.idempotency;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, Long> {
    Optional<IdempotencyRecord> findByCustomerIdAndOperationAndKeyHash(
            long customerId, String operation, String keyHash);
}
