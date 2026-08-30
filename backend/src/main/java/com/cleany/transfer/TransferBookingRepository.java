package com.cleany.transfer;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.Set;

import com.cleany.crossservice.rentaltransfer.RentalTransferContextType;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransferBookingRepository extends JpaRepository<TransferBooking, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select booking from TransferBooking booking where booking.id = :id")
    Optional<TransferBooking> findByIdForUpdate(@Param("id") long id);

    @EntityGraph(attributePaths = {"airport", "vehicleType", "driver"})
    List<TransferBooking> findAllByCustomerIdOrderByCreatedAtDesc(long customerId);

    @EntityGraph(attributePaths = {"airport", "vehicleType", "driver"})
    Optional<TransferBooking> findByIdAndCustomerId(long id, long customerId);

    boolean existsByCustomerIdAndSourceRentalBookingIdAndRentalContextAndStatusIn(
            long customerId,
            long sourceRentalBookingId,
            RentalTransferContextType rentalContext,
            Set<TransferBookingStatus> statuses
    );

    List<TransferBooking> findAllByCustomerIdAndSourceRentalBookingIdIsNullAndDirectionAndPickupDateAndStatusIn(
            long customerId,
            TransferDirection direction,
            LocalDate pickupDate,
            Set<TransferBookingStatus> statuses
    );

    @EntityGraph(attributePaths = {"airport", "vehicleType", "driver"})
    List<TransferBooking> findAllByOrderByPickupDateDescPickupTimeDescIdDesc();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            update transfer_booking booking
               set driver_id = :driverId,
                   status = 'CONFIRMED',
                   confirmed_at = :confirmedAt,
                   version = version + 1
             where booking.id = :bookingId
               and booking.status = 'REQUESTED'
               and booking.driver_id is null
               and exists (
                   select 1
                     from transfer_driver driver
                    where driver.id = :driverId
                      and driver.enabled
               )
            """, nativeQuery = true)
    int assignRequestedBooking(
            @Param("bookingId") long bookingId,
            @Param("driverId") long driverId,
            @Param("confirmedAt") Instant confirmedAt
    );
}
