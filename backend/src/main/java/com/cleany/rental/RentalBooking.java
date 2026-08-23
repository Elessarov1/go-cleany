package com.cleany.rental;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rental_booking")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private long customerId;

    @Column(name = "communication_identity_id", nullable = false)
    private long communicationIdentityId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private RentalProperty property;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "phone", nullable = false, length = 40)
    private String phone;

    @Column(name = "guests", nullable = false)
    private int guests;

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "base_daily_price_snapshot", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseDailyPriceSnapshot;

    @Column(name = "long_term_discount_rate_snapshot", nullable = false, precision = 5, scale = 4)
    private BigDecimal longTermDiscountRateSnapshot;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private RentalBookingStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancellation_reason", length = 1000)
    private String cancellationReason;

    @Column(name = "completed_at")
    private Instant completedAt;

    public RentalBooking(
            long customerId,
            long communicationIdentityId,
            RentalProperty property,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            String customerName,
            String phone,
            int guests,
            String comment,
            RentalPriceQuote quote,
            Instant createdAt
    ) {
        if (customerId <= 0 || communicationIdentityId <= 0) {
            throw new IllegalArgumentException("Customer and communication identity ids must be positive");
        }
        RentalPriceQuote requiredQuote = Objects.requireNonNull(quote, "quote");
        this.customerId = customerId;
        this.communicationIdentityId = communicationIdentityId;
        this.property = Objects.requireNonNull(property, "property");
        this.checkInDate = Objects.requireNonNull(checkInDate, "checkInDate");
        this.checkOutDate = Objects.requireNonNull(checkOutDate, "checkOutDate");
        this.durationDays = requiredQuote.durationDays();
        this.customerName = requireText(customerName, "customerName");
        this.phone = requireText(phone, "phone");
        if (guests <= 0) {
            throw new IllegalArgumentException("guests must be positive");
        }
        this.guests = guests;
        this.comment = normalizeOptional(comment);
        this.baseDailyPriceSnapshot = requiredQuote.baseDailyPrice();
        this.longTermDiscountRateSnapshot = requiredQuote.discountRate();
        this.discountAmount = requiredQuote.discountAmount();
        this.totalPrice = requiredQuote.totalPrice();
        this.currency = requiredQuote.currency();
        this.status = RentalBookingStatus.CONFIRMED;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public void cancelByCustomer(LocalDate today, Instant cancelledAt) {
        if (status != RentalBookingStatus.CONFIRMED
                || !Objects.requireNonNull(today, "today").isBefore(checkInDate)) {
            throw new RentalBookingCannotBeCancelledException(requireId());
        }
        status = RentalBookingStatus.CANCELLED_BY_CUSTOMER;
        this.cancelledAt = Objects.requireNonNull(cancelledAt, "cancelledAt");
        cancellationReason = null;
    }

    public void cancelByAdmin(String reason, Instant cancelledAt) {
        if (status != RentalBookingStatus.CONFIRMED) {
            throw new RentalBookingCannotBeCancelledException(requireId());
        }
        status = RentalBookingStatus.CANCELLED_BY_ADMIN;
        this.cancelledAt = Objects.requireNonNull(cancelledAt, "cancelledAt");
        cancellationReason = normalizeOptional(reason);
    }

    public void complete(LocalDate today, Instant completedAt) {
        if (status != RentalBookingStatus.CONFIRMED
                || Objects.requireNonNull(today, "today").isBefore(checkOutDate)) {
            throw new RentalBookingCannotBeCompletedException(requireId());
        }
        status = RentalBookingStatus.COMPLETED;
        this.completedAt = Objects.requireNonNull(completedAt, "completedAt");
    }

    private long requireId() {
        return id == null ? 0 : id;
    }

    private static String requireText(String value, String name) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return normalized;
    }

    private static String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
