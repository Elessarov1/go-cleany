package com.cleany.authentication;

public class SessionTokenException extends RuntimeException {
    private final String code;

    public SessionTokenException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() { return code; }
}
