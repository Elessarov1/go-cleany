package com.cleany.crossservice.rentaltransfer;

import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.cleany.common.text.AddressNormalizer;
import com.cleany.rental.RentalBooking;
import com.cleany.transfer.TransferBooking;
import com.cleany.transfer.TransferBookingRepository;
import com.cleany.transfer.TransferBookingStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class RentalTransferMatchingService {

    private static final Set<TransferBookingStatus> MATCHING_STATUSES = Collections.unmodifiableSet(
            EnumSet.of(
                    TransferBookingStatus.REQUESTED,
                    TransferBookingStatus.CONFIRMED,
                    TransferBookingStatus.COMPLETED
            )
    );

    private final TransferBookingRepository transferBookingRepository;

    boolean hasMatchingTransfer(
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
        String expectedAddress = AddressNormalizer.normalize(rentalBooking.getProperty().getAddress());
        return transferBookingRepository
                .findAllByCustomerIdAndSourceRentalBookingIdIsNullAndDirectionAndPickupDateAndStatusIn(
                        customerId,
                        context.direction(),
                        suggestedDate,
                        MATCHING_STATUSES
                )
                .stream()
                .map(TransferBooking::getAddress)
                .map(AddressNormalizer::normalize)
                .anyMatch(expectedAddress::equals);
    }

    boolean hasMatchingTransferInAnyContext(long customerId, RentalBooking rentalBooking) {
        for (RentalTransferContextType context : RentalTransferContextType.values()) {
            if (hasMatchingTransfer(customerId, rentalBooking, context)) {
                return true;
            }
        }
        return false;
    }
}
