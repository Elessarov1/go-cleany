package com.cleany.catalog;

public class PlatformServiceNotAvailableException extends RuntimeException {

    public PlatformServiceNotAvailableException(PlatformService service) {
        super("Service is not currently available: " + service);
    }
}
