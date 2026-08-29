package com.cleany.transfer;

public class TransferConfigurationUnavailableException extends RuntimeException {

    public TransferConfigurationUnavailableException(String resource) {
        super("Transfer " + resource + " is not available");
    }
}
