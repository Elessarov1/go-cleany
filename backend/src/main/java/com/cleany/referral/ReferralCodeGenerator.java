package com.cleany.referral;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class ReferralCodeGenerator {

    private static final char[] ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();
    private static final int RANDOM_LENGTH = 8;

    private final SecureRandom random = new SecureRandom();

    public String nextCode() {
        var code = new StringBuilder("GC");
        for (int index = 0; index < RANDOM_LENGTH; index++) {
            code.append(ALPHABET[random.nextInt(ALPHABET.length)]);
        }
        return code.toString();
    }
}
