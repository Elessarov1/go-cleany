package com.cleany.customer;

public class AccountDeletionBlockedException extends AccountSecurityException {
    public AccountDeletionBlockedException() {
        super("account_deletion_blocked_by_active_operation",
                "Account deletion is blocked by an active operation that cannot be cancelled");
    }
}
