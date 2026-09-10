package com.cleany.idempotency;

public class IdempotencyException extends RuntimeException {
    private final String code;

    IdempotencyException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
