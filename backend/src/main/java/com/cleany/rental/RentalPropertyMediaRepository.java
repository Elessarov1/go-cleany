package com.cleany.rental;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

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

    boolean existsByProperty_Id(long propertyId);

    boolean existsByMediaAssetIdOrCardMediaAssetIdOrThumbnailMediaAssetId(
            long mediaAssetId,
            long cardMediaAssetId,
            long thumbnailMediaAssetId
    );
}
