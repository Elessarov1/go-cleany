package com.cleany.crossservice.rentalcleaning;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.finance.OrderFinancialCalculator;
import com.cleany.order.CleaningOrder;
import com.cleany.rental.RentalBooking;
import com.cleany.rental.RentalBookingRepository;
import com.cleany.rental.RentalBookingStatus;

@Service
public class RentalCleaningBenefitService {

    private static final Logger log = LoggerFactory.getLogger(RentalCleaningBenefitService.class);

    private final RentalCleaningBenefitRepository benefitRepository;
    private final RentalBookingRepository bookingRepository;
    private final RentalCleaningBenefitProperties properties;
    private final OrderFinancialCalculator financialCalculator;

    public RentalCleaningBenefitService(
            RentalCleaningBenefitRepository benefitRepository,
            RentalBookingRepository bookingRepository,
            RentalCleaningBenefitProperties properties,
            OrderFinancialCalculator financialCalculator
    ) {
        this.benefitRepository = benefitRepository;
        this.bookingRepository = bookingRepository;
        this.properties = properties;
        this.financialCalculator = financialCalculator;
        financialCalculator.validateRentalCheckoutPromoRate(properties.discountRate());
    }

    @Transactional(readOnly = true)
    public RentalCleaningBenefitPlan quote(
            long customerId,
            String rawCode,
            LocalDate requestedDate,
            BigDecimal basePrice
    ) {
        RentalCleaningBenefit benefit = benefitRepository
                .findByCodeIgnoreCase(normalizeCode(rawCode))
                .orElseThrow(() -> notApplicable("Rental cleaning benefit is invalid"));
        validate(benefit, customerId, requestedDate);
        return plan(benefit, basePrice);
    }

    @Transactional
    public RentalCleaningBenefitPlan planForCreation(
            long customerId,
            String rawCode,
            LocalDate requestedDate,
            BigDecimal basePrice
    ) {
        RentalCleaningBenefit benefit = benefitRepository
                .findByCodeForUpdate(normalizeCode(rawCode))
                .orElseThrow(() -> notApplicable("Rental cleaning benefit is invalid"));
        validate(benefit, customerId, requestedDate);
        return plan(benefit, basePrice);
    }

    @Transactional
    public void reserve(RentalCleaningBenefitPlan plan, long cleaningOrderId) {
        RentalCleaningBenefit benefit = benefitRepository.findByIdForUpdate(plan.benefitId())
                .orElseThrow(() -> notApplicable("Rental cleaning benefit was not found"));
        benefit.reserve(cleaningOrderId);
    }

    @Transactional
    public void release(CleaningOrder order) {
        if (order.getAppliedRentalCleaningBenefitId() == null) {
            return;
        }
        benefitRepository.findByIdForUpdate(order.getAppliedRentalCleaningBenefitId())
                .ifPresent(benefit -> benefit.release(order.getId()));
    }

    @Transactional
    public void redeem(CleaningOrder order) {
        if (order.getAppliedRentalCleaningBenefitId() == null) {
            return;
        }
        RentalCleaningBenefit benefit = benefitRepository
                .findByIdForUpdate(order.getAppliedRentalCleaningBenefitId())
                .orElseThrow(() -> notApplicable("Rental cleaning benefit was not found"));
        benefit.redeem(order.getId(), order.getCompletedAt());
    }

    private void validate(
            RentalCleaningBenefit benefit,
            long customerId,
            LocalDate requestedDate
    ) {
        if (benefit.getCustomerId() != customerId) {
            rejected(benefit, "ownership");
            throw notApplicable("Rental cleaning benefit belongs to another customer");
        }
        if (benefit.getStatus() != RentalCleaningBenefitStatus.AVAILABLE) {
            rejected(benefit, "status_" + benefit.getStatus().name().toLowerCase(Locale.ROOT));
            throw notApplicable("Rental cleaning benefit is no longer available");
        }
        RentalBooking booking = bookingRepository.findById(benefit.getRentalBookingId())
                .orElseThrow(() -> notApplicable("Source rental booking was not found"));
        if (booking.getCustomerId() != benefit.getCustomerId()
                || isCancelled(booking.getStatus())) {
            rejected(benefit, "source_booking_invalid");
            throw notApplicable("Source rental booking is no longer eligible");
        }
        validateCheckoutDate(benefit, booking.getCheckOutDate(), requestedDate);
    }

    private void validateCheckoutDate(
            RentalCleaningBenefit benefit,
            LocalDate checkOutDate,
            LocalDate requestedDate
    ) {
        LocalDate earliestDate = checkOutDate.minusDays(properties.checkoutWindowDays());
        if (requestedDate == null
                || requestedDate.isBefore(earliestDate)
                || requestedDate.isAfter(checkOutDate)) {
            rejected(benefit, "outside_checkout_window");
            throw notApplicable(
                    "Cleaning date must be between " + earliestDate + " and " + checkOutDate
            );
        }
    }

    private RentalCleaningBenefitPlan plan(
            RentalCleaningBenefit benefit,
            BigDecimal basePrice
    ) {
        return new RentalCleaningBenefitPlan(
                financialCalculator.rentalCheckoutPromo(
                        basePrice,
                        properties.discountRate(),
                        properties.maxDiscount()
                ),
                benefit.getId()
        );
    }

    private void rejected(RentalCleaningBenefit benefit, String reason) {
        log.warn(
                "Rental cleaning benefit application rejected: benefitId={}, bookingId={}, reason={}",
                benefit.getId(),
                benefit.getRentalBookingId(),
                reason
        );
    }

    private static boolean isCancelled(RentalBookingStatus status) {
        return status == RentalBookingStatus.CANCELLED_BY_ADMIN
                || status == RentalBookingStatus.CANCELLED_BY_CUSTOMER;
    }

    private static RentalCleaningBenefitNotApplicableException notApplicable(String message) {
        return new RentalCleaningBenefitNotApplicableException(message);
    }

    private static String normalizeCode(String value) {
        if (value == null || value.isBlank()) {
            throw notApplicable("Rental cleaning benefit code is required");
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
