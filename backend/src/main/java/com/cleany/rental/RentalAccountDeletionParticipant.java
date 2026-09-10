package com.cleany.rental;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cleany.customer.AccountDeletionBlockedException;
import com.cleany.customer.AccountDeletionParticipant;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.ExternalIdentityProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class RentalAccountDeletionParticipant implements AccountDeletionParticipant {
    private final RentalBookingRepository repository;
    private final RentalBookingService service;
    private final RentalStayPolicy stayPolicy;

    @Override
    public void verifyCancellable(long customerId) {
        boolean blocked = locked(customerId).stream().anyMatch(booking ->
                booking.getStatus() == RentalBookingStatus.CONFIRMED
                        && !stayPolicy.today().isBefore(booking.getCheckInDate()));
        if (blocked) throw new AccountDeletionBlockedException();
    }

    @Override
    public void cancel(long customerId) {
        CurrentCustomer customer = deletionCustomer(customerId);
        locked(customerId).stream()
                .filter(booking -> booking.getStatus() == RentalBookingStatus.CONFIRMED)
                .map(RentalBooking::getId)
                .toList()
                .forEach(id -> service.cancel(customer, id));
    }

    private List<RentalBooking> locked(long customerId) {
        return repository.findAllByCustomerIdForUpdate(customerId);
    }

    private static CurrentCustomer deletionCustomer(long id) {
        return new CurrentCustomer(id, 1, ExternalIdentityProvider.GOOGLE,
                "account-deletion", null, "Deleted customer", null);
    }
}
