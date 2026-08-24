package com.cleany.crossservice.rentalcleaning;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalCleaningBenefitRepository
        extends JpaRepository<RentalCleaningBenefit, Long> {

    Optional<RentalCleaningBenefit> findByRentalBookingId(long rentalBookingId);

    Optional<RentalCleaningBenefit> findByCodeIgnoreCase(String code);

    boolean existsByRentalBookingId(long rentalBookingId);

    boolean existsByCodeIgnoreCase(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select benefit from RentalCleaningBenefit benefit where benefit.id = :id")
    Optional<RentalCleaningBenefit> findByIdForUpdate(@Param("id") long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select benefit from RentalCleaningBenefit benefit where upper(benefit.code) = upper(:code)")
    Optional<RentalCleaningBenefit> findByCodeForUpdate(@Param("code") String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select benefit from RentalCleaningBenefit benefit where benefit.rentalBookingId = :bookingId")
    Optional<RentalCleaningBenefit> findByRentalBookingIdForUpdate(
            @Param("bookingId") long bookingId
    );
}
