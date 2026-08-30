package com.cleany.repeat;

public class RepeatSourceNotEligibleException extends RuntimeException {

    public RepeatSourceNotEligibleException(String service, long sourceEntityId) {
        super(service + " source is not eligible for repeat: " + sourceEntityId);
    }
}
