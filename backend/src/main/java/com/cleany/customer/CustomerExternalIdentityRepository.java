package com.cleany.customer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerExternalIdentityRepository extends JpaRepository<CustomerExternalIdentity, Long> {

    Optional<CustomerExternalIdentity> findByProviderAndExternalSubject(
            ExternalIdentityProvider provider,
            String externalSubject
    );
}
