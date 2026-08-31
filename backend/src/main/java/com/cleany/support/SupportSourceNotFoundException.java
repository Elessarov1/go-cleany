package com.cleany.support;

public class SupportSourceNotFoundException extends RuntimeException {

    public SupportSourceNotFoundException() {
        super("Support source was not found");
    }
}
