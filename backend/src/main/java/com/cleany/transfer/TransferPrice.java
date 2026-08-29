package com.cleany.transfer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "transfer_price",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_transfer_price_route_vehicle",
                columnNames = {"airport_id", "vehicle_type_id", "direction"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "airport_id", nullable = false)
    private TransferAirport airport;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private TransferVehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TransferDirection direction;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    public TransferPrice(
            TransferAirport airport,
            TransferVehicleType vehicleType,
            TransferDirection direction,
            BigDecimal amount,
            String currency,
            boolean enabled,
            Instant createdAt
    ) {
        this.airport = Objects.requireNonNull(airport, "airport");
        this.vehicleType = Objects.requireNonNull(vehicleType, "vehicleType");
        this.direction = Objects.requireNonNull(direction, "direction");
        update(amount, currency, enabled, createdAt);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public void update(BigDecimal amount, String currency, boolean enabled, Instant updatedAt) {
        BigDecimal requiredAmount = Objects.requireNonNull(amount, "amount");
        if (requiredAmount.signum() <= 0) {
            throw new InvalidTransferConfigurationException("Transfer price must be positive");
        }
        String normalizedCurrency = Objects.requireNonNull(currency, "currency")
                .trim()
                .toUpperCase(Locale.ROOT);
        try {
            Currency.getInstance(normalizedCurrency);
        } catch (IllegalArgumentException exception) {
            throw new InvalidTransferConfigurationException("Transfer currency is invalid");
        }
        this.amount = requiredAmount;
        this.currency = normalizedCurrency;
        this.enabled = enabled;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }
}
