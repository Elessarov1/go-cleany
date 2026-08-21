package com.cleany.media;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaProviderReferenceRepository extends JpaRepository<MediaProviderReference, Long> {

    Optional<MediaProviderReference> findByProviderAndExternalId(
            MediaProvider provider,
            String externalId
    );

    Optional<MediaProviderReference> findByProviderAndExternalUniqueId(
            MediaProvider provider,
            String externalUniqueId
    );
}
