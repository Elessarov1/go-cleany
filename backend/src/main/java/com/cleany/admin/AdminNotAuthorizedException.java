package com.cleany.admin;

public class AdminNotAuthorizedException extends RuntimeException {

    public AdminNotAuthorizedException() {
        super("Administrator access is required");
    }
}
