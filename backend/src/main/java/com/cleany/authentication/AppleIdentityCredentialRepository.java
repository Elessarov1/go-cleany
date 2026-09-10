package com.cleany.authentication;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface AppleIdentityCredentialRepository extends JpaRepository<AppleIdentityCredential, Long> {
    @Query(value = """
            select credential.* from apple_identity_credential credential
              join customer_external_identity identity on identity.id = credential.identity_id
             where identity.customer_id = :customerId and identity.provider = 'APPLE'
            """, nativeQuery = true)
    List<AppleIdentityCredential> findAllForCustomer(@Param("customerId") long customerId);
}
