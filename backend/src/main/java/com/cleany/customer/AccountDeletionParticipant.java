package com.cleany.customer;

public interface AccountDeletionParticipant {
    void verifyCancellable(long customerId);
    void cancel(long customerId);

    default void afterCancellation(long customerId) {
    }
}
