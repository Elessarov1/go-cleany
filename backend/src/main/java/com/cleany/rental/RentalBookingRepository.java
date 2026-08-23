package com.cleany.rental;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalBookingRepository extends JpaRepository<RentalBooking, Long> {

    List<RentalBooking> findAllByCustomerIdOrderByCreatedAtDesc(long customerId);

    Optional<RentalBooking> findByIdAndCustomerId(long id, long customerId);

    List<RentalBooking> findAllByOrderByCreatedAtDesc();

    long countByCustomerIdAndStatusAndCheckOutDateAfter(
            long customerId,
            RentalBookingStatus status,
            LocalDate date
    );
}
