package com.cleany.authentication;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class SessionTokenCrypto {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int GCM_TAG_BITS = 128;
    private final byte[] encryptionKey;

    SessionTokenCrypto(NativeAuthProperties properties) {
        if (properties.tokenEncryptionKey() == null) {
            encryptionKey = null;
            return;
        }
        try {
            encryptionKey = Base64.getDecoder().decode(properties.tokenEncryptionKey());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Native auth encryption key must be base64", exception);
        }
        if (encryptionKey.length != 32) {
            throw new IllegalArgumentException("Native auth encryption key must contain 32 bytes");
        }
    }

    String newToken(String prefix) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return prefix + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    public String encrypt(String value) {
        requireKey();
        byte[] iv = new byte[12];
        RANDOM.nextBytes(iv);
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptionKey, "AES"),
                    new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            byte[] result = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
            return Base64.getEncoder().encodeToString(result);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Could not encrypt session refresh response", exception);
        }
    }

    public String decrypt(String value) {
        requireKey();
        byte[] source = Base64.getDecoder().decode(value);
        if (source.length <= 12) throw new IllegalArgumentException("Encrypted value is invalid");
        byte[] iv = java.util.Arrays.copyOfRange(source, 0, 12);
        byte[] encrypted = java.util.Arrays.copyOfRange(source, 12, source.length);
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(encryptionKey, "AES"),
                    new GCMParameterSpec(GCM_TAG_BITS, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Could not decrypt session refresh response", exception);
        }
    }

    private void requireKey() {
        if (encryptionKey == null) throw new IllegalStateException("Native auth is not configured");
    }
}
