package com.cleany.media;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MediaProviderReferenceRepository extends JpaRepository<MediaProviderReference, Long> {

    Optional<MediaProviderReference> findByProviderAndExternalId(
            MediaProvider provider,
            String externalId
    );

    Optional<MediaProviderReference> findByProviderAndExternalUniqueId(
            MediaProvider provider,
            String externalUniqueId
    );

    Optional<MediaProviderReference> findFirstByMediaAsset_IdAndProviderOrderByCreatedAtDescIdDesc(
            long mediaAssetId,
            MediaProvider provider
    );

    @Modifying(flushAutomatically = true)
    @Query("delete from MediaProviderReference providerReference "
            + "where providerReference.mediaAsset.id = :mediaAssetId")
    int deleteAllByMediaAssetId(@Param("mediaAssetId") long mediaAssetId);
}
