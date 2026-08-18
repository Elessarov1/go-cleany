package com.cleany.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cleaning_order")
public class CleaningOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_user_id", nullable = false)
    private long telegramUserId;

    @Column(name = "telegram_username", length = 64)
    private String telegramUsername;

    @Column(name = "customer_name", nullable = false, length = 255)
    private String customerName;

    @Column(name = "phone", nullable = false, length = 40)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "area", nullable = false, length = 32)
    private ServiceArea area;

    @Column(name = "address", nullable = false, length = 1000)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "apartment_type", nullable = false, length = 32)
    private ApartmentType apartmentType;

    @Column(name = "duplex", nullable = false)
    private boolean duplex;

    @Enumerated(EnumType.STRING)
    @Column(name = "cleaning_type", nullable = false, length = 32)
    private CleaningType cleaningType;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "requested_date", nullable = false)
    private LocalDate requestedDate;

    @Column(name = "customer_comment", length = 1000)
    private String customerComment;

    @Column(name = "cleaner_comment", length = 1000)
    private String cleanerComment;

    @Column(name = "cleaner_telegram_user_id")
    private Long cleanerTelegramUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private CleaningOrderStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "report_input_active", nullable = false)
    private boolean reportInputActive;

    protected CleaningOrder() {
    }

    CleaningOrder(
            long telegramUserId,
            String telegramUsername,
            String customerName,
            String phone,
            ServiceArea area,
            String address,
            ApartmentType apartmentType,
            boolean duplex,
            CleaningType cleaningType,
            BigDecimal price,
            String currency,
            LocalDate requestedDate,
            String customerComment,
            Instant createdAt
    ) {
        this.telegramUserId = telegramUserId;
        this.telegramUsername = telegramUsername;
        this.customerName = Objects.requireNonNull(customerName);
        this.phone = Objects.requireNonNull(phone);
        this.area = Objects.requireNonNull(area);
        this.address = Objects.requireNonNull(address);
        this.apartmentType = Objects.requireNonNull(apartmentType);
        this.duplex = duplex;
        this.cleaningType = Objects.requireNonNull(cleaningType);
        this.price = Objects.requireNonNull(price);
        this.currency = Objects.requireNonNull(currency);
        this.requestedDate = Objects.requireNonNull(requestedDate);
        this.customerComment = customerComment;
        this.status = CleaningOrderStatus.NEW;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    void cancelByCustomer() {
        requireStatus(CleaningOrderStatus.NEW, "be cancelled by the customer");
        status = CleaningOrderStatus.CANCELLED;
    }

    void cancelByCleaner(long cleanerId) {
        requireAssignedCleaner(cleanerId);
        if (status != CleaningOrderStatus.ACCEPTED && status != CleaningOrderStatus.AWAITING_REPORT) {
            throw new InvalidOrderStateException(
                    id == null ? 0 : id,
                    status,
                    "be cancelled by the assigned cleaner"
            );
        }
        reportInputActive = false;
        status = CleaningOrderStatus.CANCELLED;
    }

    void requireCanStartReport(long cleanerId) {
        requireAssignedCleaner(cleanerId);
        if (status != CleaningOrderStatus.ACCEPTED && status != CleaningOrderStatus.AWAITING_REPORT) {
            throw new InvalidOrderStateException(
                    id == null ? 0 : id,
                    status,
                    "start collecting a photo report"
            );
        }
    }

    void startReportCollection(long cleanerId) {
        requireCanStartReport(cleanerId);
        status = CleaningOrderStatus.AWAITING_REPORT;
        reportInputActive = true;
    }

    void updateCleanerComment(long cleanerId, String comment) {
        requireReportAccess(cleanerId);
        cleanerComment = comment;
    }

    void requireReportAccess(long cleanerId) {
        requireAssignedCleaner(cleanerId);
        requireStatus(CleaningOrderStatus.AWAITING_REPORT, "access the photo report");
    }

    void complete(long cleanerId, String comment, Instant completionTime) {
        requireAssignedCleaner(cleanerId);
        requireStatus(CleaningOrderStatus.AWAITING_REPORT, "be completed");
        cleanerComment = comment;
        completedAt = Objects.requireNonNull(completionTime);
        reportInputActive = false;
        status = CleaningOrderStatus.COMPLETED;
    }

    void reject() {
        requireStatus(CleaningOrderStatus.NEW, "be rejected");
        status = CleaningOrderStatus.REJECTED;
    }

    private void requireAssignedCleaner(long cleanerId) {
        if (cleanerTelegramUserId == null || cleanerTelegramUserId != cleanerId) {
            throw new CleanerNotAuthorizedException(cleanerId);
        }
    }

    private void requireStatus(CleaningOrderStatus expected, String action) {
        if (status != expected) {
            throw new InvalidOrderStateException(id == null ? 0 : id, status, action);
        }
    }

    public Long getId() {
        return id;
    }

    public long getTelegramUserId() {
        return telegramUserId;
    }

    public String getTelegramUsername() {
        return telegramUsername;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhone() {
        return phone;
    }

    public ServiceArea getArea() {
        return area;
    }

    public String getAddress() {
        return address;
    }

    public ApartmentType getApartmentType() {
        return apartmentType;
    }

    public boolean isDuplex() {
        return duplex;
    }

    public CleaningType getCleaningType() {
        return cleaningType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

    public String getCustomerComment() {
        return customerComment;
    }

    public String getCleanerComment() {
        return cleanerComment;
    }

    public Long getCleanerTelegramUserId() {
        return cleanerTelegramUserId;
    }

    public CleaningOrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public boolean isReportInputActive() {
        return reportInputActive;
    }
}
