package com.cleany.rental;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalPropertyRepository extends JpaRepository<RentalProperty, Long> {

    List<RentalProperty> findAllByOrderByCreatedAtDesc();

    List<RentalProperty> findAllByStatusOrderByCreatedAtDesc(RentalPropertyStatus status);

    Optional<RentalProperty> findBySlugAndStatus(String slug, RentalPropertyStatus status);

    Optional<RentalProperty> findByIdAndStatus(long id, RentalPropertyStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select property from RentalProperty property where property.id = :id")
    Optional<RentalProperty> findByIdForUpdate(@Param("id") long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select property
              from RentalProperty property
             where property.id = :id
               and property.status = :status
            """)
    Optional<RentalProperty> findByIdAndStatusForUpdate(
            @Param("id") long id,
            @Param("status") RentalPropertyStatus status
    );

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsBySlugIgnoreCaseAndIdNot(String slug, long id);
}
