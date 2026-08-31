package com.cleany.support;

public class SupportCaseNotFoundException extends RuntimeException {

    public SupportCaseNotFoundException(long caseId) {
        super("Support case not found: " + caseId);
    }
}
