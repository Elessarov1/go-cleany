package com.cleany.crossservice.rentalcleaning;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class RentalCleaningBenefitCodeGenerator {

    private static final char[] ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();
    private static final int RANDOM_LENGTH = 8;

    private final SecureRandom random = new SecureRandom();

    public String nextCode() {
        var code = new StringBuilder("RC");
        for (int index = 0; index < RANDOM_LENGTH; index++) {
            code.append(ALPHABET[random.nextInt(ALPHABET.length)]);
        }
        return code.toString();
    }
}
