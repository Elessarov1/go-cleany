package com.cleany.transfer;

import static com.cleany.common.text.TextValues.normalizeOptional;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transfer_airport")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferAirport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 8, unique = true)
    private String code;

    @Column(name = "name_ru", nullable = false)
    private String nameRu;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    public TransferAirport(
            String code,
            String nameRu,
            String nameEn,
            boolean enabled,
            int sortOrder,
            Instant createdAt
    ) {
        this.code = requireText(code, "code").toUpperCase(Locale.ROOT);
        update(nameRu, nameEn, enabled, sortOrder, createdAt);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public void update(String nameRu, String nameEn, boolean enabled, int sortOrder, Instant updatedAt) {
        if (sortOrder < 0) {
            throw new InvalidTransferConfigurationException("Airport sort order cannot be negative");
        }
        this.nameRu = requireText(nameRu, "nameRu");
        this.nameEn = requireText(nameEn, "nameEn");
        this.enabled = enabled;
        this.sortOrder = sortOrder;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    private static String requireText(String value, String field) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            throw new InvalidTransferConfigurationException(field + " must not be blank");
        }
        return normalized;
    }
}
