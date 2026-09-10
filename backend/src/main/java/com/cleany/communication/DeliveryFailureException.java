package com.cleany.communication;

import java.time.Duration;

public class DeliveryFailureException extends RuntimeException {
    private final String code;
    private final boolean retryable;
    private final boolean invalidEndpoint;
    private final Duration retryAfter;

    public DeliveryFailureException(String code, boolean retryable, boolean invalidEndpoint,
                                    Duration retryAfter, Throwable cause) {
        super(code, cause);
        this.code = code;
        this.retryable = retryable;
        this.invalidEndpoint = invalidEndpoint;
        this.retryAfter = retryAfter;
    }

    public String code() { return code; }
    public boolean retryable() { return retryable; }
    public boolean invalidEndpoint() { return invalidEndpoint; }
    public Duration retryAfter() { return retryAfter; }
}
