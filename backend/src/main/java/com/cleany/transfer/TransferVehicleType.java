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
@Table(name = "transfer_vehicle_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferVehicleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32, unique = true)
    private String code;

    @Column(name = "name_ru", nullable = false)
    private String nameRu;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "max_passengers", nullable = false)
    private int maxPassengers;

    @Column(name = "max_luggage", nullable = false)
    private int maxLuggage;

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

    public TransferVehicleType(
            String code,
            String nameRu,
            String nameEn,
            int maxPassengers,
            int maxLuggage,
            boolean enabled,
            int sortOrder,
            Instant createdAt
    ) {
        this.code = requireText(code, "code").toUpperCase(Locale.ROOT);
        update(nameRu, nameEn, maxPassengers, maxLuggage, enabled, sortOrder, createdAt);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public void update(
            String nameRu,
            String nameEn,
            int maxPassengers,
            int maxLuggage,
            boolean enabled,
            int sortOrder,
            Instant updatedAt
    ) {
        if (maxPassengers < 1 || maxLuggage < 0 || sortOrder < 0) {
            throw new InvalidTransferConfigurationException("Vehicle capacity or sort order is invalid");
        }
        this.nameRu = requireText(nameRu, "nameRu");
        this.nameEn = requireText(nameEn, "nameEn");
        this.maxPassengers = maxPassengers;
        this.maxLuggage = maxLuggage;
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
