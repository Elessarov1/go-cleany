package com.cleany.crossservice.rentaltransfer;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
import com.cleany.transfer.TransferBooking;
import com.cleany.transfer.TransferBookingPolicy;
import com.cleany.transfer.TransferBookingRepository;
import com.cleany.transfer.TransferBookingStatus;
import com.cleany.transfer.TransferDirection;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalTransferContextService {

    private static final Set<TransferBookingStatus> MATCHING_STATUSES = Collections.unmodifiableSet(
            EnumSet.of(
                    TransferBookingStatus.REQUESTED,
                    TransferBookingStatus.CONFIRMED,
                    TransferBookingStatus.COMPLETED
            )
    );

    private final CustomerAccountService customerAccountService;
    private final RentalBookingRepository rentalBookingRepository;
    private final TransferBookingRepository transferBookingRepository;
    private final TransferBookingPolicy transferBookingPolicy;
    private final PlatformServiceAccessService serviceAccessService;
    private final RentalTransferActionTrackingService trackingService;

    @Transactional(readOnly = true)
    public RentalTransferContextResponse currentCustomerContext(long rentalBookingId) {
        return context(customerAccountService.currentCustomer(), rentalBookingId);
    }

    @Transactional(readOnly = true)
    public RentalTransferContextResponse context(CurrentCustomer customer, long rentalBookingId) {
        RentalBooking booking = requireOwnedBooking(customer, rentalBookingId);
        boolean flowAvailable = serviceAccessService.canStartCustomerFlow(
                PlatformService.TRANSFER,
                customer.customerId()
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
                .map(context -> visibleOption(customer, booking, context))
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
        resolveBookable(customer, rentalBookingId, context);
        trackingService.record(
                customer.customerId(),
                rentalBookingId,
                context,
                RentalTransferActionEventType.CTA_SHOWN
        );
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
        return prefill(booking, context);
    }

    @Transactional(readOnly = true)
    public ResolvedRentalTransferSource resolveForCreation(
            CurrentCustomer customer,
            RentalTransferSourceRequest source,
            TransferDirection requestedDirection
    ) {
        RentalTransferContextType context = source.context();
        if (context.direction() != requestedDirection) {
            throw new RentalTransferContextNotEligibleException(
                    source.bookingId(),
                    "transfer direction does not match the selected context"
            );
        }
        resolveBookable(customer, source.bookingId(), context);
        return new ResolvedRentalTransferSource(source.bookingId(), context);
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
        if (booking.getStatus() != RentalBookingStatus.CONFIRMED) {
            throw ineligible(booking, "rental booking is not confirmed");
        }
        LocalDate suggestedDate = context.suggestedDate(booking);
        if (!transferBookingPolicy.isBookableDate(suggestedDate)) {
            throw ineligible(booking, "suggested date is outside the current transfer booking window");
        }
        if (hasMatchingTransfer(customer.customerId(), booking, context)) {
            throw new RentalTransferAlreadyBookedException(booking.getId(), context);
        }
        return booking;
    }

    private RentalTransferContextOptionResponse visibleOption(
            CurrentCustomer customer,
            RentalBooking booking,
            RentalTransferContextType context
    ) {
        LocalDate suggestedDate = context.suggestedDate(booking);
        if (hasMatchingTransfer(customer.customerId(), booking, context)
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
                bookable ? null : transferBookingPolicy.bookingOpensOn(suggestedDate)
        );
    }

    private boolean hasMatchingTransfer(
            long customerId,
            RentalBooking rentalBooking,
            RentalTransferContextType context
    ) {
        if (transferBookingRepository.existsByCustomerIdAndSourceRentalBookingIdAndRentalContextAndStatusIn(
                customerId,
                rentalBooking.getId(),
                context,
                MATCHING_STATUSES
        )) {
            return true;
        }
        LocalDate suggestedDate = context.suggestedDate(rentalBooking);
        String expectedAddress = comparableAddress(rentalBooking.getProperty().getAddress());
        return transferBookingRepository
                .findAllByCustomerIdAndSourceRentalBookingIdIsNullAndDirectionAndPickupDateAndStatusIn(
                        customerId,
                        context.direction(),
                        suggestedDate,
                        MATCHING_STATUSES
                )
                .stream()
                .map(TransferBooking::getAddress)
                .map(RentalTransferContextService::comparableAddress)
                .anyMatch(expectedAddress::equals);
    }

    private RentalBooking requireOwnedBooking(CurrentCustomer customer, long rentalBookingId) {
        return rentalBookingRepository.findByIdAndCustomerId(rentalBookingId, customer.customerId())
                .orElseThrow(() -> new RentalBookingNotFoundException(rentalBookingId));
    }

    private static RentalTransferPrefillResponse prefill(
            RentalBooking booking,
            RentalTransferContextType context
    ) {
        return new RentalTransferPrefillResponse(
                booking.getId(),
                context,
                context.direction(),
                context.suggestedDate(booking),
                booking.getProperty().getAddress()
        );
    }

    private static RentalTransferContextNotEligibleException ineligible(
            RentalBooking booking,
            String reason
    ) {
        return new RentalTransferContextNotEligibleException(booking.getId(), reason);
    }

    private static String comparableAddress(String value) {
        return Normalizer.normalize(value.strip(), Normalizer.Form.NFKC)
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
