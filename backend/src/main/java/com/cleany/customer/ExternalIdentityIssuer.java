package com.cleany.customer;

public final class ExternalIdentityIssuer {

    public static final String GOOGLE = "https://accounts.google.com";
    public static final String APPLE = "https://appleid.apple.com";
    public static final String TELEGRAM = "https://telegram.org";

    private ExternalIdentityIssuer() {
    }

    public static String canonical(ExternalIdentityProvider provider) {
        return switch (provider) {
            case GOOGLE -> GOOGLE;
            case APPLE -> APPLE;
            case TELEGRAM -> TELEGRAM;
        };
    }
}
