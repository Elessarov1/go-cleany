package com.cleany.transfer;

import static com.cleany.common.text.TextValues.normalizeOptional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
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
import jakarta.persistence.Version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transfer_booking")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private long customerId;

    @Column(name = "communication_identity_id", nullable = false)
    private long communicationIdentityId;

    @Column(name = "customer_name_snapshot", nullable = false)
    private String customerNameSnapshot;

    @Column(name = "customer_phone_snapshot", nullable = false, length = 40)
    private String customerPhoneSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TransferDirection direction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "airport_id", nullable = false)
    private TransferAirport airport;

    @Column(name = "airport_code_snapshot", nullable = false, length = 8)
    private String airportCodeSnapshot;

    @Column(name = "airport_name_ru_snapshot", nullable = false)
    private String airportNameRuSnapshot;

    @Column(name = "airport_name_en_snapshot", nullable = false)
    private String airportNameEnSnapshot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private TransferVehicleType vehicleType;

    @Column(name = "vehicle_code_snapshot", nullable = false, length = 32)
    private String vehicleCodeSnapshot;

    @Column(name = "vehicle_name_ru_snapshot", nullable = false)
    private String vehicleNameRuSnapshot;

    @Column(name = "vehicle_name_en_snapshot", nullable = false)
    private String vehicleNameEnSnapshot;

    @Column(name = "pickup_date", nullable = false)
    private LocalDate pickupDate;

    @Column(name = "pickup_time", nullable = false)
    private LocalTime pickupTime;

    @Column(nullable = false, length = 1000)
    private String address;

    @Column(name = "passenger_count", nullable = false)
    private int passengerCount;

    @Column(name = "luggage_count", nullable = false)
    private int luggageCount;

    @Column(name = "flight_number", length = 64)
    private String flightNumber;

    @Column(name = "scheduled_arrival_time")
    private LocalTime scheduledArrivalTime;

    @Column(length = 1000)
    private String comment;

    @Column(name = "price_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceAmount;

    @Column(name = "price_currency", nullable = false, length = 3)
    private String priceCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TransferBookingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private TransferDriver driver;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "rejected_at")
    private Instant rejectedAt;

    @Column(name = "status_reason", length = 1000)
    private String statusReason;

    @Version
    @Column(nullable = false)
    private long version;

    public TransferBooking(NewTransferBooking booking) {
        NewTransferBooking required = Objects.requireNonNull(booking, "booking");
        if (required.customerId() <= 0 || required.communicationIdentityId() <= 0) {
            throw invalid("Customer and communication identity ids must be positive");
        }
        this.customerId = required.customerId();
        this.communicationIdentityId = required.communicationIdentityId();
        this.customerNameSnapshot = requireText(required.customerName(), "Customer name");
        this.customerPhoneSnapshot = requireText(required.customerPhone(), "Customer phone");
        this.direction = Objects.requireNonNull(required.direction(), "direction");
        this.airport = Objects.requireNonNull(required.airport(), "airport");
        this.vehicleType = Objects.requireNonNull(required.vehicleType(), "vehicleType");
        validateConfigurationAndCapacity(required);
        this.airportCodeSnapshot = airport.getCode();
        this.airportNameRuSnapshot = airport.getNameRu();
        this.airportNameEnSnapshot = airport.getNameEn();
        this.vehicleCodeSnapshot = vehicleType.getCode();
        this.vehicleNameRuSnapshot = vehicleType.getNameRu();
        this.vehicleNameEnSnapshot = vehicleType.getNameEn();
        this.pickupDate = Objects.requireNonNull(required.pickupDate(), "pickupDate");
        this.pickupTime = Objects.requireNonNull(required.pickupTime(), "pickupTime");
        this.address = requireText(required.address(), "Address");
        this.passengerCount = required.passengerCount();
        this.luggageCount = required.luggageCount();
        this.flightNumber = normalizeOptional(required.flightNumber());
        this.scheduledArrivalTime = required.scheduledArrivalTime();
        validateFlightData();
        this.comment = normalizeOptional(required.comment());
        this.priceAmount = required.price().getAmount();
        this.priceCurrency = required.price().getCurrency();
        this.status = TransferBookingStatus.REQUESTED;
        this.createdAt = Objects.requireNonNull(required.createdAt(), "createdAt");
    }

    public void assignDriver(TransferDriver driver, Instant confirmedAt) {
        if (status != TransferBookingStatus.REQUESTED || !Objects.requireNonNull(driver, "driver").isEnabled()) {
            throw new TransferBookingStateException(requireId(), "assigned");
        }
        this.driver = driver;
        status = TransferBookingStatus.CONFIRMED;
        this.confirmedAt = Objects.requireNonNull(confirmedAt, "confirmedAt");
    }

    public void reject(String reason, Instant rejectedAt) {
        if (status != TransferBookingStatus.REQUESTED) {
            throw new TransferBookingStateException(requireId(), "rejected");
        }
        status = TransferBookingStatus.REJECTED;
        statusReason = normalizeOptional(reason);
        this.rejectedAt = Objects.requireNonNull(rejectedAt, "rejectedAt");
    }

    public void cancelByCustomer(boolean pickupStarted, Instant cancelledAt) {
        if (pickupStarted || status != TransferBookingStatus.REQUESTED
                && status != TransferBookingStatus.CONFIRMED) {
            throw new TransferBookingStateException(requireId(), "cancelled by customer");
        }
        cancel(null, cancelledAt);
    }

    public void cancelByAdmin(String reason, Instant cancelledAt) {
        if (status != TransferBookingStatus.REQUESTED && status != TransferBookingStatus.CONFIRMED) {
            throw new TransferBookingStateException(requireId(), "cancelled by admin");
        }
        cancel(reason, cancelledAt);
    }

    public void complete(boolean pickupStarted, Instant completedAt) {
        if (!pickupStarted || status != TransferBookingStatus.CONFIRMED) {
            throw new TransferBookingStateException(requireId(), "completed");
        }
        status = TransferBookingStatus.COMPLETED;
        this.completedAt = Objects.requireNonNull(completedAt, "completedAt");
    }

    private void cancel(String reason, Instant cancelledAt) {
        status = TransferBookingStatus.CANCELLED;
        statusReason = normalizeOptional(reason);
        this.cancelledAt = Objects.requireNonNull(cancelledAt, "cancelledAt");
    }

    private void validateConfigurationAndCapacity(NewTransferBooking booking) {
        TransferPrice requiredPrice = Objects.requireNonNull(booking.price(), "price");
        if (!airport.isEnabled() || !vehicleType.isEnabled() || !requiredPrice.isEnabled()
                || requiredPrice.getAirport() != airport
                || requiredPrice.getVehicleType() != vehicleType
                || requiredPrice.getDirection() != direction) {
            throw invalid("Transfer configuration is not active or does not match the booking");
        }
        if (booking.passengerCount() < 1 || booking.passengerCount() > vehicleType.getMaxPassengers()) {
            throw invalid("Passenger count exceeds vehicle capacity");
        }
        if (booking.luggageCount() < 0 || booking.luggageCount() > vehicleType.getMaxLuggage()) {
            throw invalid("Luggage count exceeds vehicle capacity");
        }
    }

    private void validateFlightData() {
        if (direction == TransferDirection.FROM_AIRPORT
                && (flightNumber == null || scheduledArrivalTime == null)) {
            throw invalid("Flight number and scheduled arrival time are required for airport pickup");
        }
    }

    private long requireId() {
        return id == null ? 0 : id;
    }

    private static InvalidTransferBookingException invalid(String message) {
        return new InvalidTransferBookingException(message);
    }

    private static String requireText(String value, String field) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            throw invalid(field + " must not be blank");
        }
        return normalized;
    }
}
