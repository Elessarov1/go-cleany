package com.cleany.customer;

public class AccountLinkTokenExpiredException extends RuntimeException {

    public AccountLinkTokenExpiredException() {
        super("Account link token has expired");
    }
}
