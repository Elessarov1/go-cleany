package com.cleany.crossservice.rentaltransfer;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalTransferBenefitRepository extends JpaRepository<RentalTransferBenefit, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select benefit from RentalTransferBenefit benefit where benefit.rentalBookingId = :rentalBookingId")
    Optional<RentalTransferBenefit> findByRentalBookingIdForUpdate(
            @Param("rentalBookingId") long rentalBookingId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select benefit from RentalTransferBenefit benefit where benefit.transferBookingId = :transferBookingId")
    Optional<RentalTransferBenefit> findByTransferBookingIdForUpdate(
            @Param("transferBookingId") long transferBookingId
    );
}
