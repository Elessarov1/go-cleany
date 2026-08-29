package com.cleany.transfer;

public class TransferConfigurationNotFoundException extends RuntimeException {

    public TransferConfigurationNotFoundException(String resource, long id) {
        super("Transfer " + resource + " not found: " + id);
    }
}
