package com.cleany.order;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cleany.customer.AccountDeletionBlockedException;
import com.cleany.customer.AccountDeletionParticipant;
import com.cleany.customer.CurrentCustomer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class CleaningAccountDeletionParticipant implements AccountDeletionParticipant {
    private final CleaningOrderRepository repository;
    private final CleaningOrderService service;

    @Override
    public void verifyCancellable(long customerId) {
        boolean blocked = locked(customerId).stream().anyMatch(order -> switch (order.getStatus()) {
            case ACCEPTED, AWAITING_REPORT, ONSITE_ISSUE_REPORTED -> true;
            default -> false;
        });
        if (blocked) throw new AccountDeletionBlockedException();
    }

    @Override
    public void cancel(long customerId) {
        CurrentCustomer customer = deletionCustomer(customerId);
        locked(customerId).stream()
                .filter(order -> order.getStatus() == CleaningOrderStatus.NEW)
                .map(CleaningOrder::getId)
                .toList()
                .forEach(id -> service.cancel(customer, id));
    }

    private List<CleaningOrder> locked(long customerId) {
        return repository.findAllByCustomerIdForUpdate(customerId);
    }

    private static CurrentCustomer deletionCustomer(long id) {
        return new CurrentCustomer(id, 1, com.cleany.customer.ExternalIdentityProvider.GOOGLE,
                "account-deletion", null, "Deleted customer", null);
    }
}
