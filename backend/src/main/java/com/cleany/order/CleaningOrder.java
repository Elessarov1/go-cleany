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
import jakarta.persistence.Version;

import com.cleany.finance.AcquisitionSource;
import com.cleany.finance.CustomerDiscountType;
import com.cleany.finance.OrderFinancialSnapshot;

@Entity
@Table(name = "cleaning_order")
public class CleaningOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private long customerId;

    @Column(name = "communication_identity_id", nullable = false)
    private long communicationIdentityId;

    @Column(name = "repeat_source_order_id")
    private Long repeatSourceOrderId;

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

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "commission_rate", nullable = false, precision = 7, scale = 6)
    private BigDecimal commissionRate;

    @Column(name = "base_commission", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseCommission;

    @Column(name = "customer_discount", nullable = false, precision = 12, scale = 2)
    private BigDecimal customerDiscount;

    @Column(name = "partner_payout", nullable = false, precision = 12, scale = 2)
    private BigDecimal partnerPayout;

    @Column(name = "final_customer_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal finalCustomerPrice;

    @Column(name = "platform_net", nullable = false, precision = 12, scale = 2)
    private BigDecimal platformNet;

    @Enumerated(EnumType.STRING)
    @Column(name = "acquisition_source", nullable = false, length = 24)
    private AcquisitionSource acquisitionSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_discount_type", nullable = false, length = 32)
    private CustomerDiscountType customerDiscountType;

    @Column(name = "referral_code_id")
    private Long referralCodeId;

    @Column(name = "referrer_customer_id")
    private Long referrerCustomerId;

    @Column(name = "referral_partner_id")
    private Long referralPartnerId;

    @Column(name = "applied_reward_id")
    private Long appliedRewardId;

    @Column(name = "applied_rental_cleaning_benefit_id")
    private Long appliedRentalCleaningBenefitId;

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

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected CleaningOrder() {
    }

    CleaningOrder(
            long customerId,
            long communicationIdentityId,
            String customerName,
            String phone,
            ServiceArea area,
            String address,
            ApartmentType apartmentType,
            boolean duplex,
            CleaningType cleaningType,
            OrderFinancialSnapshot financialSnapshot,
            Long referralCodeId,
            Long referrerCustomerId,
            Long referralPartnerId,
            Long appliedRewardId,
            String currency,
            LocalDate requestedDate,
            String customerComment,
            Instant createdAt
    ) {
        this(
                customerId,
                communicationIdentityId,
                customerName,
                phone,
                area,
                address,
                apartmentType,
                duplex,
                cleaningType,
                financialSnapshot,
                referralCodeId,
                referrerCustomerId,
                referralPartnerId,
                appliedRewardId,
                null,
                currency,
                requestedDate,
                customerComment,
                createdAt
        );
    }

    CleaningOrder(
            long customerId,
            long communicationIdentityId,
            String customerName,
            String phone,
            ServiceArea area,
            String address,
            ApartmentType apartmentType,
            boolean duplex,
            CleaningType cleaningType,
            OrderFinancialSnapshot financialSnapshot,
            Long referralCodeId,
            Long referrerCustomerId,
            Long referralPartnerId,
            Long appliedRewardId,
            Long appliedRentalCleaningBenefitId,
            String currency,
            LocalDate requestedDate,
            String customerComment,
            Instant createdAt
    ) {
        this.customerId = customerId;
        if (communicationIdentityId <= 0) {
            throw new IllegalArgumentException("communicationIdentityId must be positive");
        }
        this.communicationIdentityId = communicationIdentityId;
        this.customerName = Objects.requireNonNull(customerName);
        this.phone = Objects.requireNonNull(phone);
        this.area = Objects.requireNonNull(area);
        this.address = Objects.requireNonNull(address);
        this.apartmentType = Objects.requireNonNull(apartmentType);
        this.duplex = duplex;
        this.cleaningType = Objects.requireNonNull(cleaningType);
        var financials = Objects.requireNonNull(financialSnapshot);
        this.basePrice = financials.basePrice();
        this.commissionRate = financials.commissionRate();
        this.baseCommission = financials.baseCommission();
        this.customerDiscount = financials.customerDiscount();
        this.partnerPayout = financials.partnerPayout();
        this.finalCustomerPrice = financials.finalCustomerPrice();
        this.platformNet = financials.platformNet();
        this.acquisitionSource = financials.acquisitionSource();
        this.customerDiscountType = financials.customerDiscountType();
        this.price = financials.finalCustomerPrice();
        this.referralCodeId = referralCodeId;
        this.referrerCustomerId = referrerCustomerId;
        this.referralPartnerId = referralPartnerId;
        this.appliedRewardId = appliedRewardId;
        this.appliedRentalCleaningBenefitId = appliedRentalCleaningBenefitId;
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

    void markRepeatOf(long sourceOrderId) {
        if (sourceOrderId <= 0) {
            throw new IllegalArgumentException("Repeat source order id must be positive");
        }
        repeatSourceOrderId = sourceOrderId;
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

    void requireCanReportOnsiteIssue(long cleanerId) {
        requireAssignedCleaner(cleanerId);
        requireStatus(CleaningOrderStatus.ACCEPTED, "report an onsite issue");
    }

    void reportOnsiteIssue(long cleanerId) {
        requireCanReportOnsiteIssue(cleanerId);
        reportInputActive = false;
        status = CleaningOrderStatus.ONSITE_ISSUE_REPORTED;
    }

    void resolveOnsiteIssue() {
        requireStatus(CleaningOrderStatus.ONSITE_ISSUE_REPORTED, "resolve an onsite issue");
        reportInputActive = false;
        status = CleaningOrderStatus.CANCELLED;
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

    public long getCustomerId() {
        return customerId;
    }

    public long getCommunicationIdentityId() {
        return communicationIdentityId;
    }

    public Long getRepeatSourceOrderId() {
        return repeatSourceOrderId;
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

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public BigDecimal getBaseCommission() {
        return baseCommission;
    }

    public BigDecimal getCustomerDiscount() {
        return customerDiscount;
    }

    public BigDecimal getPartnerPayout() {
        return partnerPayout;
    }

    public BigDecimal getFinalCustomerPrice() {
        return finalCustomerPrice;
    }

    public BigDecimal getPlatformNet() {
        return platformNet;
    }

    public AcquisitionSource getAcquisitionSource() {
        return acquisitionSource;
    }

    public CustomerDiscountType getCustomerDiscountType() {
        return customerDiscountType;
    }

    public Long getReferralCodeId() {
        return referralCodeId;
    }

    public Long getReferrerCustomerId() {
        return referrerCustomerId;
    }

    public Long getReferralPartnerId() {
        return referralPartnerId;
    }

    public Long getAppliedRewardId() {
        return appliedRewardId;
    }

    public Long getAppliedRentalCleaningBenefitId() {
        return appliedRentalCleaningBenefitId;
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

    public long getVersion() {
        return version;
    }
}
