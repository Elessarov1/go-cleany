package com.cleany.customer;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select customer from CustomerAccount customer where customer.id = :id")
    Optional<CustomerAccount> findByIdForUpdate(@Param("id") long id);
}
