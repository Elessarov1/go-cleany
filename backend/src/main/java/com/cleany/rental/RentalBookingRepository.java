package com.cleany.rental;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalBookingRepository extends JpaRepository<RentalBooking, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select booking from RentalBooking booking where booking.id = :id")
    Optional<RentalBooking> findByIdForUpdate(@Param("id") long id);

    @Query(value = """
            select booking.id
            from rental_booking booking
            where booking.status = 'CONFIRMED'
              and booking.check_out_date between :today and :windowEnd
              and not exists (
                  select 1
                  from rental_cleaning_benefit benefit
                  where benefit.rental_booking_id = booking.id
              )
            order by booking.id
            limit :batchSize
            """, nativeQuery = true)
    List<Long> findRentalCleaningBenefitCandidates(
            @Param("today") LocalDate today,
            @Param("windowEnd") LocalDate windowEnd,
            @Param("batchSize") int batchSize
    );

    @Query(value = """
            select booking.id
              from rental_booking booking
             where booking.status = 'CONFIRMED'
               and booking.check_out_date between :today and :windowEnd
             order by booking.check_out_date, booking.id
             limit :batchSize
            """, nativeQuery = true)
    List<Long> findCheckoutReminderCandidates(
            @Param("today") LocalDate today,
            @Param("windowEnd") LocalDate windowEnd,
            @Param("batchSize") int batchSize
    );

    List<RentalBooking> findAllByCustomerIdOrderByCreatedAtDesc(long customerId);

    Optional<RentalBooking> findByIdAndCustomerId(long id, long customerId);

    List<RentalBooking> findAllByOrderByCreatedAtDesc();

    boolean existsByProperty_Id(long propertyId);

    long countByCustomerIdAndStatusAndCheckOutDateAfter(
            long customerId,
            RentalBookingStatus status,
            LocalDate date
    );
}
