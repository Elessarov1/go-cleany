package com.cleany.customer;

public class AccountLinkTokenInvalidException extends RuntimeException {

    public AccountLinkTokenInvalidException() {
        super("Account link token is invalid");
    }
}
