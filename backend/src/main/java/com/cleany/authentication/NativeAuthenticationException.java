package com.cleany.authentication;

public class NativeAuthenticationException extends RuntimeException {
    private final String code;

    public NativeAuthenticationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() { return code; }
}
