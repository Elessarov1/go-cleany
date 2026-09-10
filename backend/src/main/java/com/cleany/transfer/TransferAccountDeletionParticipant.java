package com.cleany.transfer;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cleany.customer.AccountDeletionBlockedException;
import com.cleany.customer.AccountDeletionParticipant;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.ExternalIdentityProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class TransferAccountDeletionParticipant implements AccountDeletionParticipant {
    private final TransferBookingRepository repository;
    private final TransferBookingService service;
    private final TransferBookingPolicy policy;

    @Override
    public void verifyCancellable(long customerId) {
        boolean blocked = locked(customerId).stream().anyMatch(booking ->
                (booking.getStatus() == TransferBookingStatus.REQUESTED
                        || booking.getStatus() == TransferBookingStatus.CONFIRMED)
                        && policy.hasStarted(booking));
        if (blocked) throw new AccountDeletionBlockedException();
    }

    @Override
    public void cancel(long customerId) {
        CurrentCustomer customer = deletionCustomer(customerId);
        locked(customerId).stream()
                .filter(booking -> booking.getStatus() == TransferBookingStatus.REQUESTED
                        || booking.getStatus() == TransferBookingStatus.CONFIRMED)
                .map(TransferBooking::getId)
                .toList()
                .forEach(id -> service.cancel(customer, id));
    }

    private List<TransferBooking> locked(long customerId) {
        return repository.findAllByCustomerIdForUpdate(customerId);
    }

    private static CurrentCustomer deletionCustomer(long id) {
        return new CurrentCustomer(id, 1, ExternalIdentityProvider.GOOGLE,
                "account-deletion", null, "Deleted customer", null);
    }
}
