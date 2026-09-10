package com.cleany.customer;

public class AccountSecurityException extends RuntimeException {
    private final String code;

    public AccountSecurityException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
