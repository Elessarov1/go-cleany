package com.cleany.rental;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalPropertyMediaRepository extends JpaRepository<RentalPropertyMedia, Long> {

    List<RentalPropertyMedia> findAllByProperty_IdOrderBySortOrderAscIdAsc(long propertyId);

    Optional<RentalPropertyMedia> findByIdAndProperty_Id(long id, long propertyId);

    Optional<RentalPropertyMedia> findByIdAndProperty_IdAndProperty_Status(
            long id,
            long propertyId,
            RentalPropertyStatus status
    );

    boolean existsByProperty_Id(long propertyId);
}
