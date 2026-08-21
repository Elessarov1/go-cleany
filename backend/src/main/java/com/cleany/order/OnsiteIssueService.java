package com.cleany.order;

import java.time.Clock;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.configuration.CleanerProperties;
import com.cleany.configuration.OnsiteIssueProperties;
import com.cleany.media.MediaProvider;
import com.cleany.media.MediaProviderReferenceService;
import com.cleany.media.MediaUpload;
import com.cleany.referral.ReferralService;

@Service
public class OnsiteIssueService {

    private static final int MAX_COMMENT_LENGTH = 1000;

    private final CleaningOrderRepository orderRepository;
    private final CleaningOrderIssueReportRepository reportRepository;
    private final CleaningOrderIssuePhotoRepository photoRepository;
    private final CleaningOrderEventRepository eventRepository;
    private final CleanerProperties cleanerProperties;
    private final OnsiteIssueProperties properties;
    private final ReferralService referralService;
    private final MediaProviderReferenceService mediaProviderReferenceService;
    private final Clock clock;
    private final ApplicationEventPublisher eventPublisher;

    public OnsiteIssueService(
            CleaningOrderRepository orderRepository,
            CleaningOrderIssueReportRepository reportRepository,
            CleaningOrderIssuePhotoRepository photoRepository,
            CleaningOrderEventRepository eventRepository,
            CleanerProperties cleanerProperties,
            OnsiteIssueProperties properties,
            ReferralService referralService,
            MediaProviderReferenceService mediaProviderReferenceService,
            Clock clock,
            ApplicationEventPublisher eventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.reportRepository = reportRepository;
        this.photoRepository = photoRepository;
        this.eventRepository = eventRepository;
        this.cleanerProperties = cleanerProperties;
        this.properties = properties;
        this.referralService = referralService;
        this.mediaProviderReferenceService = mediaProviderReferenceService;
        this.clock = clock;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public CleaningOrder start(long orderId, long cleanerTelegramUserId) {
        return requireAcceptedAssignedOrder(orderId, cleanerTelegramUserId);
    }

    @Transactional
    public OnsiteIssueProgress selectReason(
            long orderId,
            long cleanerTelegramUserId,
            OnsiteIssueReason reason
    ) {
        CleaningOrder order = requireAcceptedAssignedOrder(orderId, cleanerTelegramUserId);
        if (reason == null) {
            throw invalid(OnsiteIssueProblem.REASON_REQUIRED, "Onsite issue reason is required");
        }
        reportRepository.deactivateOtherDrafts(cleanerTelegramUserId, orderId);
        CleaningOrderIssueReport report = reportRepository.findByOrder_Id(orderId)
                .map(existing -> {
                    existing.selectReason(reason);
                    return existing;
                })
                .orElseGet(() -> reportRepository.save(new CleaningOrderIssueReport(
                        order,
                        cleanerTelegramUserId,
                        reason,
                        clock.instant()
                )));
        return progress(report);
    }

    @Transactional(readOnly = true)
    public boolean hasActiveDraft(long cleanerTelegramUserId) {
        return reportRepository
                .findByCleanerTelegramUserIdAndInputActiveTrueAndSubmittedAtIsNullAndOrder_Status(
                        cleanerTelegramUserId,
                        CleaningOrderStatus.ACCEPTED
                )
                .isPresent();
    }

    @Transactional
    public OnsiteIssueProgress addPhoto(
            long cleanerTelegramUserId,
            String telegramFileId,
            String telegramFileUniqueId,
            byte[] content,
            String caption
    ) {
        CleaningOrderIssueReport report = requireActiveDraft(cleanerTelegramUserId);
        report.getOrder().requireCanReportOnsiteIssue(cleanerTelegramUserId);
        String fileId = requireValue(telegramFileId, 512, "Telegram photo file_id");
        String uniqueId = requireValue(telegramFileUniqueId, 255, "Telegram photo file_unique_id");

        ValidatedPhoto photo = validatePhoto(content);
        var providerMedia = mediaProviderReferenceService.resolveOrStore(
                new MediaUpload(content, photo.contentType()),
                MediaProvider.TELEGRAM,
                fileId,
                uniqueId
        );

        if (!photoRepository.existsByIssueReport_IdAndMediaAssetId(
                report.getId(),
                providerMedia.media().mediaId()
        )) {
            long currentCount = photoRepository.countByIssueReport_Id(report.getId());
            if (currentCount >= properties.maxPhotos()) {
                throw invalid(
                        OnsiteIssueProblem.MAX_PHOTOS_EXCEEDED,
                        "Onsite issue report cannot contain more than " + properties.maxPhotos() + " photos"
                );
            }
            CleaningOrderIssuePhoto saved = photoRepository.save(new CleaningOrderIssuePhoto(
                    report,
                    providerMedia.media().mediaId(),
                    clock.instant()
            ));
            recordEvent(
                    report.getOrder(),
                    OrderEventType.ISSUE_PHOTO_ADDED,
                    CleaningOrderStatus.ACCEPTED,
                    CleaningOrderStatus.ACCEPTED,
                    OrderActorType.CLEANER,
                    cleanerTelegramUserId,
                    "issuePhotoId=" + saved.getId()
            );
        }

        String normalizedCaption = normalizeComment(caption, false);
        if (normalizedCaption != null) {
            report.updateComment(normalizedCaption);
        }
        return progress(report);
    }

    @Transactional
    public OnsiteIssueProgress updateComment(long cleanerTelegramUserId, String comment) {
        CleaningOrderIssueReport report = requireActiveDraft(cleanerTelegramUserId);
        report.getOrder().requireCanReportOnsiteIssue(cleanerTelegramUserId);
        report.updateComment(normalizeComment(comment, true));
        return progress(report);
    }

    @Transactional
    public OnsiteIssueDelivery submit(long orderId, long cleanerTelegramUserId) {
        requireConfiguredCleaner(cleanerTelegramUserId);
        CleaningOrder order = findOrder(orderId);
        CleaningOrderIssueReport report = reportRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> invalid(
                        OnsiteIssueProblem.COLLECTION_NOT_ACTIVE,
                        "Onsite issue report has not been started"
                ));

        if (report.getSubmittedAt() != null
                && order.getStatus() == CleaningOrderStatus.ONSITE_ISSUE_REPORTED
                && report.getCleanerTelegramUserId() == cleanerTelegramUserId) {
            return delivery(report);
        }

        order.requireCanReportOnsiteIssue(cleanerTelegramUserId);
        if (report.getCleanerTelegramUserId() != cleanerTelegramUserId || !report.isInputActive()) {
            throw new CleanerNotAuthorizedException(cleanerTelegramUserId);
        }
        String comment = normalizeComment(report.getComment(), true);
        long photoCount = photoRepository.countByIssueReport_Id(report.getId());
        if (photoCount < properties.minPhotos()) {
            throw invalid(
                    OnsiteIssueProblem.MIN_PHOTOS_REQUIRED,
                    "Onsite issue report requires at least " + properties.minPhotos() + " photos"
            );
        }

        var submittedAt = clock.instant();
        order.reportOnsiteIssue(cleanerTelegramUserId);
        report.updateComment(comment);
        report.submit(submittedAt);
        referralService.releaseReward(order);
        recordEvent(
                order,
                OrderEventType.ONSITE_ISSUE_REPORTED,
                CleaningOrderStatus.ACCEPTED,
                CleaningOrderStatus.ONSITE_ISSUE_REPORTED,
                OrderActorType.CLEANER,
                cleanerTelegramUserId,
                "reason=" + report.getReason()
        );
        recordEvent(
                order,
                OrderEventType.ISSUE_REPORT_SUBMITTED,
                CleaningOrderStatus.ONSITE_ISSUE_REPORTED,
                CleaningOrderStatus.ONSITE_ISSUE_REPORTED,
                OrderActorType.CLEANER,
                cleanerTelegramUserId,
                "reason=" + report.getReason() + "; photos=" + photoCount
        );
        eventPublisher.publishEvent(new CleaningOrderCustomerEvent.OnsiteIssueReported(
                order.getId(),
                order.getCustomerId(),
                order.getCommunicationIdentityId(),
                cleanerTelegramUserId
        ));
        return delivery(report);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordCustomerNotified(long orderId, long cleanerTelegramUserId) {
        CleaningOrderIssueReport report = reportRepository.findByOrder_IdAndSubmittedAtIsNotNull(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (report.getCleanerTelegramUserId() != cleanerTelegramUserId) {
            throw new CleanerNotAuthorizedException(cleanerTelegramUserId);
        }
        recordEvent(
                report.getOrder(),
                OrderEventType.ISSUE_CUSTOMER_NOTIFIED,
                CleaningOrderStatus.ONSITE_ISSUE_REPORTED,
                CleaningOrderStatus.ONSITE_ISSUE_REPORTED,
                OrderActorType.SYSTEM,
                null,
                "Onsite issue report delivered to customer"
        );
    }

    @Transactional
    public CleaningOrderIssueReport resolve(long orderId, long adminTelegramUserId, String resolutionComment) {
        CleaningOrder order = findOrder(orderId);
        CleaningOrderIssueReport report = reportRepository.findByOrder_IdAndSubmittedAtIsNotNull(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        String comment = normalizeComment(resolutionComment, true);
        order.resolveOnsiteIssue();
        report.resolve(adminTelegramUserId, comment, clock.instant());
        recordEvent(
                order,
                OrderEventType.ISSUE_RESOLVED,
                CleaningOrderStatus.ONSITE_ISSUE_REPORTED,
                CleaningOrderStatus.CANCELLED,
                OrderActorType.ADMIN,
                adminTelegramUserId,
                comment
        );
        return report;
    }

    private CleaningOrder requireAcceptedAssignedOrder(long orderId, long cleanerTelegramUserId) {
        requireConfiguredCleaner(cleanerTelegramUserId);
        CleaningOrder order = findOrder(orderId);
        order.requireCanReportOnsiteIssue(cleanerTelegramUserId);
        return order;
    }

    private CleaningOrder findOrder(long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private CleaningOrderIssueReport requireActiveDraft(long cleanerTelegramUserId) {
        requireConfiguredCleaner(cleanerTelegramUserId);
        return reportRepository
                .findByCleanerTelegramUserIdAndInputActiveTrueAndSubmittedAtIsNullAndOrder_Status(
                        cleanerTelegramUserId,
                        CleaningOrderStatus.ACCEPTED
                )
                .orElseThrow(() -> invalid(
                        OnsiteIssueProblem.COLLECTION_NOT_ACTIVE,
                        "No active onsite issue report"
                ));
    }

    private OnsiteIssueProgress progress(CleaningOrderIssueReport report) {
        long photoCount = photoRepository.countByIssueReport_Id(report.getId());
        boolean commentPresent = report.getComment() != null && !report.getComment().isBlank();
        return new OnsiteIssueProgress(
                report.getOrder().getId(),
                report.getReason(),
                photoCount,
                commentPresent,
                commentPresent && photoCount >= properties.minPhotos()
        );
    }

    private OnsiteIssueDelivery delivery(CleaningOrderIssueReport report) {
        return new OnsiteIssueDelivery(
                report.getOrder(),
                report.getReason(),
                report.getComment()
        );
    }

    private ValidatedPhoto validatePhoto(byte[] content) {
        if (content == null || content.length == 0) {
            throw invalid(OnsiteIssueProblem.PHOTO_EMPTY, "Evidence photo content must not be empty");
        }
        if (content.length > properties.maxPhotoSize().toBytes()) {
            throw invalid(
                    OnsiteIssueProblem.PHOTO_TOO_LARGE,
                    "Evidence photo exceeds " + properties.maxPhotoSize()
            );
        }
        String contentType = detectContentType(content);
        if (contentType == null || !properties.supportedContentTypes().contains(contentType)) {
            throw invalid(
                    OnsiteIssueProblem.PHOTO_TYPE_UNSUPPORTED,
                    "Evidence photo must be JPEG or PNG"
            );
        }
        return new ValidatedPhoto(contentType);
    }

    private static String detectContentType(byte[] content) {
        if (content.length >= 3
                && Byte.toUnsignedInt(content[0]) == 0xFF
                && Byte.toUnsignedInt(content[1]) == 0xD8
                && Byte.toUnsignedInt(content[2]) == 0xFF) {
            return "image/jpeg";
        }
        byte[] pngSignature = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        if (content.length >= pngSignature.length) {
            for (int index = 0; index < pngSignature.length; index++) {
                if (content[index] != pngSignature[index]) {
                    return null;
                }
            }
            return "image/png";
        }
        return null;
    }

    private static String normalizeComment(String value, boolean required) {
        String normalized = value == null || value.isBlank() ? null : value.trim();
        if (required && normalized == null) {
            throw invalid(OnsiteIssueProblem.COMMENT_REQUIRED, "Onsite issue comment is required");
        }
        if (normalized != null && normalized.length() > MAX_COMMENT_LENGTH) {
            throw invalid(OnsiteIssueProblem.COMMENT_REQUIRED, "Onsite issue comment is too long");
        }
        return normalized;
    }

    private static String requireValue(String value, int maxLength, String name) {
        if (value == null || value.isBlank() || value.trim().length() > maxLength) {
            throw invalid(OnsiteIssueProblem.PHOTO_EMPTY, name + " is invalid");
        }
        return value.trim();
    }

    private void requireConfiguredCleaner(long cleanerTelegramUserId) {
        if (!cleanerProperties.contains(cleanerTelegramUserId)) {
            throw new CleanerNotAuthorizedException(cleanerTelegramUserId);
        }
    }

    private void recordEvent(
            CleaningOrder order,
            OrderEventType eventType,
            CleaningOrderStatus fromStatus,
            CleaningOrderStatus toStatus,
            OrderActorType actorType,
            Long actorTelegramUserId,
            String details
    ) {
        eventRepository.save(new CleaningOrderEvent(
                order,
                eventType,
                fromStatus,
                toStatus,
                actorType,
                actorTelegramUserId,
                details,
                clock.instant()
        ));
    }

    private static InvalidOnsiteIssueException invalid(OnsiteIssueProblem problem, String message) {
        return new InvalidOnsiteIssueException(problem, message);
    }

    private record ValidatedPhoto(String contentType) {
    }
}
