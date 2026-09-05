package com.cleany.crossservice.rentaltransfer;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.catalog.PlatformService;
import com.cleany.catalog.PlatformServiceAccessService;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.rental.RentalBooking;
import com.cleany.rental.RentalBookingNotFoundException;
import com.cleany.rental.RentalBookingRepository;
import com.cleany.rental.RentalBookingStatus;
import com.cleany.transfer.TransferBookingPolicy;
import com.cleany.transfer.TransferDirection;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalTransferContextService {

    private final CustomerAccountService customerAccountService;
    private final RentalBookingRepository rentalBookingRepository;
    private final TransferBookingPolicy transferBookingPolicy;
    private final PlatformServiceAccessService serviceAccessService;
    private final RentalTransferActionTrackingService trackingService;
    private final RentalTransferMatchingService matchingService;
    private final RentalTransferBenefitService benefitService;

    @Transactional(readOnly = true)
    public RentalTransferContextResponse currentCustomerContext(long rentalBookingId) {
        return context(customerAccountService.currentCustomer(), rentalBookingId);
    }

    @Transactional(readOnly = true)
    public RentalTransferContextResponse context(CurrentCustomer customer, long rentalBookingId) {
        return context(customer.customerId(), rentalBookingId);
    }

    @Transactional(readOnly = true)
    public RentalTransferContextResponse contextForCustomer(long customerId, long rentalBookingId) {
        return context(customerId, rentalBookingId);
    }

    private RentalTransferContextResponse context(long customerId, long rentalBookingId) {
        RentalBooking booking = requireOwnedBooking(customerId, rentalBookingId);
        boolean flowAvailable = serviceAccessService.canStartCustomerFlow(
                PlatformService.TRANSFER,
                customerId
        );
        if (!flowAvailable || booking.getStatus() != RentalBookingStatus.CONFIRMED) {
            return new RentalTransferContextResponse(
                    booking.getId(),
                    flowAvailable,
                    Collections.emptyList()
            );
        }
        List<RentalTransferContextOptionResponse> options = Arrays.stream(
                        RentalTransferContextType.values()
                )
                .map(context -> visibleOption(customerId, booking, context))
                .filter(java.util.Objects::nonNull)
                .toList();
        return new RentalTransferContextResponse(booking.getId(), true, options);
    }

    @Transactional
    public void recordShown(long rentalBookingId, RentalTransferContextType context) {
        recordShown(customerAccountService.currentCustomer(), rentalBookingId, context);
    }

    @Transactional
    public void recordShown(
            CurrentCustomer customer,
            long rentalBookingId,
            RentalTransferContextType context
    ) {
        RentalBooking booking = resolveBookable(customer, rentalBookingId, context);
        trackingService.record(
                customer.customerId(),
                rentalBookingId,
                context,
                RentalTransferActionEventType.CTA_SHOWN
        );
        if (benefitService.visibleBenefit(customer.customerId(), booking) != null) {
            trackingService.record(
                    customer.customerId(),
                    rentalBookingId,
                    context,
                    RentalTransferActionEventType.BENEFIT_SHOWN
            );
        }
    }

    @Transactional
    public RentalTransferPrefillResponse prefill(
            long rentalBookingId,
            RentalTransferContextType context
    ) {
        return prefill(customerAccountService.currentCustomer(), rentalBookingId, context);
    }

    @Transactional
    public RentalTransferPrefillResponse prefill(
            CurrentCustomer customer,
            long rentalBookingId,
            RentalTransferContextType context
    ) {
        RentalBooking booking = resolveBookable(customer, rentalBookingId, context);
        trackingService.record(
                customer.customerId(),
                rentalBookingId,
                context,
                RentalTransferActionEventType.PREFILL_STARTED
        );
        RentalTransferBenefitResponse benefit = benefitService.visibleBenefit(
                customer.customerId(),
                booking
        );
        if (benefit != null) {
            trackingService.record(
                    customer.customerId(),
                    rentalBookingId,
                    context,
                    RentalTransferActionEventType.BENEFIT_PREFILL_STARTED
            );
        }
        return prefill(booking, context, benefit);
    }

    @Transactional
    public ResolvedRentalTransferSource resolveForCreation(
            CurrentCustomer customer,
            RentalTransferSourceRequest source,
            TransferDirection requestedDirection
    ) {
        requireMatchingDirection(source, requestedDirection);
        RentalBooking booking = resolveBookableForUpdate(customer, source.bookingId(), source.context());
        return new ResolvedRentalTransferSource(source.bookingId(), source.context(), booking);
    }

    @Transactional(readOnly = true)
    public ResolvedRentalTransferSource resolveForQuote(
            CurrentCustomer customer,
            RentalTransferSourceRequest source,
            TransferDirection requestedDirection
    ) {
        requireMatchingDirection(source, requestedDirection);
        RentalBooking booking = resolveBookable(customer, source.bookingId(), source.context());
        return new ResolvedRentalTransferSource(source.bookingId(), source.context(), booking);
    }

    private RentalBooking resolveBookable(
            CurrentCustomer customer,
            long rentalBookingId,
            RentalTransferContextType context
    ) {
        serviceAccessService.requireCanStartCustomerFlow(
                PlatformService.TRANSFER,
                customer.customerId()
        );
        RentalBooking booking = requireOwnedBooking(customer, rentalBookingId);
        requireBookable(customer.customerId(), booking, context);
        return booking;
    }

    private RentalBooking resolveBookableForUpdate(
            CurrentCustomer customer,
            long rentalBookingId,
            RentalTransferContextType context
    ) {
        serviceAccessService.requireCanStartCustomerFlow(
                PlatformService.TRANSFER,
                customer.customerId()
        );
        RentalBooking booking = rentalBookingRepository.findByIdForUpdate(rentalBookingId)
                .filter(candidate -> candidate.getCustomerId() == customer.customerId())
                .orElseThrow(() -> new RentalBookingNotFoundException(rentalBookingId));
        requireBookable(customer.customerId(), booking, context);
        return booking;
    }

    private void requireBookable(
            long customerId,
            RentalBooking booking,
            RentalTransferContextType context
    ) {
        if (booking.getStatus() != RentalBookingStatus.CONFIRMED) {
            throw ineligible(booking, "rental booking is not confirmed");
        }
        LocalDate suggestedDate = context.suggestedDate(booking);
        if (!transferBookingPolicy.isBookableDate(suggestedDate)) {
            throw ineligible(booking, "suggested date is outside the current transfer booking window");
        }
        if (matchingService.hasMatchingTransfer(customerId, booking, context)) {
            throw new RentalTransferAlreadyBookedException(booking.getId(), context);
        }
    }

    private RentalTransferContextOptionResponse visibleOption(
            long customerId,
            RentalBooking booking,
            RentalTransferContextType context
    ) {
        LocalDate suggestedDate = context.suggestedDate(booking);
        if (matchingService.hasMatchingTransfer(customerId, booking, context)
                || suggestedDate.isBefore(transferBookingPolicy.earliestBookingDate())) {
            return null;
        }
        boolean bookable = transferBookingPolicy.isBookableDate(suggestedDate);
        return new RentalTransferContextOptionResponse(
                context,
                bookable
                        ? RentalTransferContextAvailability.BOOKABLE
                        : RentalTransferContextAvailability.AVAILABLE_LATER,
                context.direction(),
                suggestedDate,
                booking.getProperty().getAddress(),
                bookable ? null : transferBookingPolicy.bookingOpensOn(suggestedDate),
                bookable ? benefitService.visibleBenefit(customerId, booking) : null
        );
    }

    private RentalBooking requireOwnedBooking(CurrentCustomer customer, long rentalBookingId) {
        return requireOwnedBooking(customer.customerId(), rentalBookingId);
    }

    private RentalBooking requireOwnedBooking(long customerId, long rentalBookingId) {
        return rentalBookingRepository.findByIdAndCustomerId(rentalBookingId, customerId)
                .orElseThrow(() -> new RentalBookingNotFoundException(rentalBookingId));
    }

    private static RentalTransferPrefillResponse prefill(
            RentalBooking booking,
            RentalTransferContextType context,
            RentalTransferBenefitResponse benefit
    ) {
        return new RentalTransferPrefillResponse(
                booking.getId(),
                context,
                context.direction(),
                context.suggestedDate(booking),
                booking.getProperty().getAddress(),
                benefit
        );
    }

    private static void requireMatchingDirection(
            RentalTransferSourceRequest source,
            TransferDirection requestedDirection
    ) {
        if (source.context().direction() != requestedDirection) {
            throw new RentalTransferContextNotEligibleException(
                    source.bookingId(),
                    "transfer direction does not match the selected context"
            );
        }
    }

    private static RentalTransferContextNotEligibleException ineligible(
            RentalBooking booking,
            String reason
    ) {
        return new RentalTransferContextNotEligibleException(booking.getId(), reason);
    }
}
