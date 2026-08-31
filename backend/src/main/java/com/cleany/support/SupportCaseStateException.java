package com.cleany.support;

public class SupportCaseStateException extends RuntimeException {

    public SupportCaseStateException(long caseId, SupportCaseStatus status) {
        super("Support case " + caseId + " cannot be changed from status " + status);
    }
}
