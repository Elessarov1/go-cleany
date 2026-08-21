package com.cleany.media;

import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "media_asset")
public class MediaAsset {

    private static final int MAX_CONTENT_TYPE_LENGTH = 100;
    private static final Pattern SHA256_PATTERN = Pattern.compile("^[0-9a-f]{64}$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false, columnDefinition = "bytea")
    private byte[] content;

    @Column(name = "content_type", nullable = false, length = MAX_CONTENT_TYPE_LENGTH)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "sha256", nullable = false, length = 64)
    private String sha256;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected MediaAsset() {
    }

    public MediaAsset(byte[] content, String contentType, String sha256, Instant createdAt) {
        byte[] requiredContent = Objects.requireNonNull(content, "content");
        if (requiredContent.length == 0) {
            throw new IllegalArgumentException("content must not be empty");
        }
        this.content = Arrays.copyOf(requiredContent, requiredContent.length);
        this.contentType = normalizeContentType(contentType);
        this.sizeBytes = requiredContent.length;
        this.sha256 = normalizeSha256(sha256);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public Long getId() {
        return id;
    }

    public byte[] getContent() {
        return Arrays.copyOf(content, content.length);
    }

    public String getContentType() {
        return contentType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public String getSha256() {
        return sha256;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private static String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("contentType must not be blank");
        }
        String normalized = contentType.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() > MAX_CONTENT_TYPE_LENGTH) {
            throw new IllegalArgumentException("contentType is too long");
        }
        return normalized;
    }

    private static String normalizeSha256(String sha256) {
        if (sha256 == null) {
            throw new IllegalArgumentException("sha256 must not be null");
        }
        String normalized = sha256.trim().toLowerCase(Locale.ROOT);
        if (!SHA256_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("sha256 must contain 64 hexadecimal characters");
        }
        return normalized;
    }
}
