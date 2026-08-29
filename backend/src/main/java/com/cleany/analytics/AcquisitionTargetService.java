package com.cleany.analytics;

public enum AcquisitionTargetService {
    PLATFORM("/"),
    CLEANING("/cleaning"),
    RENTAL("/rent");

    private final String targetPath;

    AcquisitionTargetService(String targetPath) {
        this.targetPath = targetPath;
    }

    public String targetPath() {
        return targetPath;
    }
}
