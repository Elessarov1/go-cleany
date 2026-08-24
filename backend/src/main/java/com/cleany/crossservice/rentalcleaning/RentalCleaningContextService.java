package com.cleany.crossservice.rentalcleaning;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.rental.RentalBooking;
import com.cleany.rental.RentalBookingNotFoundException;
import com.cleany.rental.RentalBookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalCleaningContextService {

    private final CustomerAccountService customerAccountService;
    private final RentalBookingRepository bookingRepository;
    private final RentalCleaningBenefitRepository benefitRepository;
    private final RentalCleaningBenefitProperties properties;

    @Transactional(readOnly = true)
    public RentalCleaningContextResponse currentCustomerContext(long rentalBookingId) {
        return context(customerAccountService.currentCustomer(), rentalBookingId);
    }

    @Transactional(readOnly = true)
    public RentalCleaningContextResponse context(
            CurrentCustomer customer,
            long rentalBookingId
    ) {
        RentalBooking booking = bookingRepository
                .findByIdAndCustomerId(rentalBookingId, customer.customerId())
                .orElseThrow(() -> new RentalBookingNotFoundException(rentalBookingId));
        RentalCleaningBenefit benefit = benefitRepository
                .findByRentalBookingId(rentalBookingId)
                .orElse(null);
        return new RentalCleaningContextResponse(
                booking.getId(),
                booking.getProperty().getAddress(),
                booking.getPhone(),
                booking.getCheckOutDate(),
                booking.getCheckOutDate().minusDays(properties.checkoutWindowDays()),
                benefit == null ? null : benefit.getStatus(),
                visibleCode(benefit)
        );
    }

    private static String visibleCode(RentalCleaningBenefit benefit) {
        return benefit != null && benefit.getStatus() == RentalCleaningBenefitStatus.AVAILABLE
                ? benefit.getCode()
                : null;
    }
}
