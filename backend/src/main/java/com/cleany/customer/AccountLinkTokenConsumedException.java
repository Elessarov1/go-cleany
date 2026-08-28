package com.cleany.customer;

public class AccountLinkTokenConsumedException extends RuntimeException {

    public AccountLinkTokenConsumedException() {
        super("Account link token has already been used");
    }
}
