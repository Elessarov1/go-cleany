package com.cleany.order;

public class InvalidOnsiteIssueException extends RuntimeException {

    private final OnsiteIssueProblem problem;

    public InvalidOnsiteIssueException(OnsiteIssueProblem problem, String message) {
        super(message);
        this.problem = problem;
    }

    public OnsiteIssueProblem getProblem() {
        return problem;
    }
}
