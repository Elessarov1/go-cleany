package com.cleany.authorization;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRoleRepository extends JpaRepository<CustomerRole, CustomerRoleId> {

    boolean existsByCustomerIdAndRole(long customerId, PlatformRole role);

    List<CustomerRole> findAllByCustomerId(long customerId);

    @Modifying(flushAutomatically = true)
    @Query(value = """
            insert into customer_role (customer_id, role, created_at)
            values (:customerId, :role, :createdAt)
            on conflict (customer_id, role) do nothing
            """, nativeQuery = true)
    int ensureRole(long customerId, String role, Instant createdAt);
}
