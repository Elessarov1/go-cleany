package com.cleany.order;

public class InvalidPhoneNumberException extends RuntimeException {

    public InvalidPhoneNumberException() {
        super("Phone number must be a valid international number with country code");
    }
}
