package com.cleany.rental;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalPropertyMediaRepository extends JpaRepository<RentalPropertyMedia, Long> {

    List<RentalPropertyMedia> findAllByProperty_IdOrderBySortOrderAscIdAsc(long propertyId);

    List<RentalPropertyMedia> findAllByProperty_IdInOrderByProperty_IdAscSortOrderAscIdAsc(
            List<Long> propertyIds
    );

    List<RentalPropertyMedia> findAllByProperty_IdInAndCoverTrueOrderByProperty_IdAscIdAsc(
            List<Long> propertyIds
    );

    Optional<RentalPropertyMedia> findByIdAndProperty_Id(long id, long propertyId);

    Optional<RentalPropertyMedia> findByIdAndProperty_IdAndProperty_Status(
            long id,
            long propertyId,
            RentalPropertyStatus status
    );

    @Query(value = """
            select media.*
              from rental_property_media media
             where media.card_media_asset_id is null
                or media.thumbnail_media_asset_id is null
             order by media.id
             limit :batchSize
             for update of media
            """, nativeQuery = true)
    List<RentalPropertyMedia> lockNextMissingVariants(@Param("batchSize") int batchSize);

    boolean existsByProperty_Id(long propertyId);

    boolean existsByMediaAssetIdOrCardMediaAssetIdOrThumbnailMediaAssetId(
            long mediaAssetId,
            long cardMediaAssetId,
            long thumbnailMediaAssetId
    );
}
