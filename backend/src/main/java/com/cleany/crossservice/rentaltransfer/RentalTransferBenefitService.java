package com.cleany.crossservice.rentaltransfer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.rental.RentalBooking;
import com.cleany.rental.RentalBookingStatus;
import com.cleany.transfer.TransferBenefitType;
import com.cleany.transfer.TransferBooking;
import com.cleany.transfer.TransferBookingStatus;
import com.cleany.transfer.TransferPrice;
import com.cleany.transfer.TransferPriceQuote;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalTransferBenefitService {

    private static final int MONEY_SCALE = 2;

    private final RentalTransferBenefitProperties properties;
    private final RentalTransferBenefitRepository benefitRepository;
    private final RentalTransferMatchingService matchingService;
    private final Clock clock;

    @Transactional(readOnly = true)
    public RentalTransferBenefitResponse visibleBenefit(long customerId, RentalBooking rentalBooking) {
        return isAvailable(customerId, rentalBooking)
                ? new RentalTransferBenefitResponse(
                        TransferBenefitType.RENTAL_FIRST_TRANSFER,
                        properties.discountRate()
                )
                : null;
    }

    @Transactional(readOnly = true)
    public TransferPriceQuote quote(
            long customerId,
            ResolvedRentalTransferSource source,
            TransferPrice price,
            TransferBenefitType requestedBenefit
    ) {
        if (requestedBenefit == null) {
            return TransferPriceQuote.standard(price);
        }
        if (requestedBenefit != TransferBenefitType.RENTAL_FIRST_TRANSFER
                || source == null
                || !isAvailable(customerId, source.booking())) {
            throw unavailable(source);
        }
        BigDecimal baseAmount = price.getAmount().setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal discountAmount = baseAmount
                .multiply(properties.discountRate())
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        return new TransferPriceQuote(
                baseAmount,
                discountAmount,
                baseAmount.subtract(discountAmount),
                price.getCurrency(),
                requestedBenefit,
                properties.discountRate()
        );
    }

    @Transactional
    public void reserve(ResolvedRentalTransferSource source, TransferBooking booking) {
        if (booking.getAppliedBenefit() != TransferBenefitType.RENTAL_FIRST_TRANSFER) {
            return;
        }
        long rentalBookingId = source.rentalBookingId();
        Instant now = clock.instant();
        RentalTransferBenefit benefit = benefitRepository
                .findByRentalBookingIdForUpdate(rentalBookingId)
                .orElse(null);
        if (benefit == null) {
            benefitRepository.save(new RentalTransferBenefit(
                    rentalBookingId,
                    booking.getCustomerId(),
                    booking.getId(),
                    now
            ));
            return;
        }
        if (benefit.getCustomerId() != booking.getCustomerId()
                || benefit.getStatus() != RentalTransferBenefitStatus.RELEASED) {
            throw new RentalTransferBenefitUnavailableException(rentalBookingId);
        }
        benefit.reserve(booking.getId(), now);
    }

    @Transactional
    public void synchronize(TransferBooking booking) {
        if (booking.getAppliedBenefit() != TransferBenefitType.RENTAL_FIRST_TRANSFER) {
            return;
        }
        benefitRepository.findByTransferBookingIdForUpdate(booking.getId()).ifPresent(benefit -> {
            if (booking.getStatus() == TransferBookingStatus.CONFIRMED
                    || booking.getStatus() == TransferBookingStatus.COMPLETED) {
                benefit.consume(clock.instant());
            } else if (booking.getStatus() == TransferBookingStatus.CANCELLED
                    || booking.getStatus() == TransferBookingStatus.REJECTED) {
                benefit.release(clock.instant());
            }
        });
    }

    private boolean isAvailable(long customerId, RentalBooking rentalBooking) {
        if (!properties.enabled()) {
            return false;
        }
        if (rentalBooking.getCustomerId() != customerId
                || rentalBooking.getStatus() != RentalBookingStatus.CONFIRMED) {
            return false;
        }
        RentalTransferBenefit benefit = benefitRepository.findById(rentalBooking.getId()).orElse(null);
        if (benefit != null && benefit.getStatus() != RentalTransferBenefitStatus.RELEASED) {
            return false;
        }
        return !matchingService.hasMatchingTransferInAnyContext(customerId, rentalBooking);
    }

    private static RentalTransferBenefitUnavailableException unavailable(
            ResolvedRentalTransferSource source
    ) {
        return new RentalTransferBenefitUnavailableException(source == null ? 0 : source.rentalBookingId());
    }
}
