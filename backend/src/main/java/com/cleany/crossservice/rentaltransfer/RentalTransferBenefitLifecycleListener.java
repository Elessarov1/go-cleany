package com.cleany.crossservice.rentaltransfer;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.transfer.TransferBookingCustomerEvent;
import com.cleany.transfer.TransferBookingRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class RentalTransferBenefitLifecycleListener {

    private final TransferBookingRepository bookingRepository;
    private final RentalTransferBenefitService benefitService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void bookingStatusChanged(TransferBookingCustomerEvent event) {
        bookingRepository.findById(event.bookingId()).ifPresent(benefitService::synchronize);
    }
}
