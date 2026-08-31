package com.cleany.support;

public class FeedbackAlreadySubmittedException extends RuntimeException {

    public FeedbackAlreadySubmittedException() {
        super("Feedback has already been submitted for this transaction");
    }
}
