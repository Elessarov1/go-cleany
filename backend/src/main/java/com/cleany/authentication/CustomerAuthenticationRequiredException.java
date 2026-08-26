package com.cleany.authentication;

public class CustomerAuthenticationRequiredException extends RuntimeException {

    public CustomerAuthenticationRequiredException() {
        super("Customer authentication is required");
    }
}
