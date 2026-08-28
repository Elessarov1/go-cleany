package com.cleany.customer;

import java.util.Optional;
import java.util.List;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerExternalIdentityRepository extends JpaRepository<CustomerExternalIdentity, Long> {

    Optional<CustomerExternalIdentity> findByIdAndCustomerId(long id, long customerId);

    Optional<CustomerExternalIdentity> findByProviderAndExternalSubject(
            ExternalIdentityProvider provider,
            String externalSubject
    );

    Optional<CustomerExternalIdentity> findByCustomerIdAndProvider(
            long customerId,
            ExternalIdentityProvider provider
    );

    List<CustomerExternalIdentity> findAllByCustomerIdOrderByProvider(long customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select identity from CustomerExternalIdentity identity
             where identity.provider = :provider and identity.externalSubject = :externalSubject
            """)
    Optional<CustomerExternalIdentity> findByProviderAndExternalSubjectForUpdate(
            @Param("provider") ExternalIdentityProvider provider,
            @Param("externalSubject") String externalSubject
    );

    @Query(value = """
            select identity.*
              from customer_external_identity identity
              join customer_role role
                on role.customer_id = identity.customer_id and role.role = 'ADMIN'
              left join rental_admin_notification_preference preference
                on preference.customer_id = identity.customer_id
             where identity.provider = 'TELEGRAM'
               and identity.write_access_allowed = true
               and coalesce(preference.telegram_enabled, true) = true
            """, nativeQuery = true)
    List<CustomerExternalIdentity> findEligibleAdminTelegramIdentities();
}
