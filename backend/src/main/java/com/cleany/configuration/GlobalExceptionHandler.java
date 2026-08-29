package com.cleany.configuration;

import java.time.Clock;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitNotApplicableException;
import com.cleany.analytics.AcquisitionCampaignNotFoundException;
import com.cleany.analytics.InvalidAcquisitionCampaignException;
import com.cleany.analytics.InvalidAnalyticsPeriodException;
import com.cleany.catalog.PlatformServiceNotAvailableException;
import com.cleany.order.BookingDateNotAvailableException;
import com.cleany.admin.AdminNotAuthorizedException;
import com.cleany.order.CleanerNotAuthorizedException;
import com.cleany.order.InvalidPhoneNumberException;
import com.cleany.order.InvalidOrderStateException;
import com.cleany.order.InvalidOnsiteIssueException;
import com.cleany.order.OrderClaimConflictException;
import com.cleany.order.OrderNotFoundException;
import com.cleany.order.CleaningReportExpiredException;
import com.cleany.referral.ReferralNotApplicableException;
import com.cleany.rental.InvalidRentalBookingException;
import com.cleany.rental.InvalidRentalDateRangeException;
import com.cleany.rental.InvalidRentalOccupancyException;
import com.cleany.rental.InvalidRentalPropertyMediaException;
import com.cleany.rental.RentalActiveBookingLimitExceededException;
import com.cleany.rental.RentalBookingCannotBeCancelledException;
import com.cleany.rental.RentalBookingCannotBeCompletedException;
import com.cleany.rental.RentalBookingHorizonExceededException;
import com.cleany.rental.RentalBookingNotFoundException;
import com.cleany.rental.RentalDatesNotAvailableException;
import com.cleany.rental.RentalMaximumStayExceededException;
import com.cleany.rental.RentalMinimumStayNotMetException;
import com.cleany.rental.RentalOccupancyNotFoundException;
import com.cleany.rental.RentalPropertyCannotBePublishedException;
import com.cleany.rental.RentalPropertyCannotBeDeletedException;
import com.cleany.rental.RentalPropertyCannotBeUnpublishedException;
import com.cleany.rental.RentalPropertyMediaNotFoundException;
import com.cleany.rental.RentalPropertyNotFoundException;
import com.cleany.rental.RentalPropertyNotAvailableException;
import com.cleany.authentication.CustomerAuthenticationRequiredException;
import com.cleany.telegram.bot.TelegramWebhookAuthenticationException;
import com.cleany.customer.AccountLinkConflictException;
import com.cleany.customer.AccountLinkProviderException;
import com.cleany.customer.AccountLinkTokenConsumedException;
import com.cleany.customer.AccountLinkTokenExpiredException;
import com.cleany.customer.AccountLinkTokenInvalidException;
import com.cleany.customer.TelegramIdentityNotLinkedException;
import com.cleany.notification.CustomerNotificationNotFoundException;
import com.cleany.transfer.InvalidTransferBookingException;
import com.cleany.transfer.TransferBookingNotFoundException;
import com.cleany.transfer.TransferBookingStateException;
import com.cleany.transfer.TransferConfigurationUnavailableException;
import com.cleany.transfer.TransferConfigurationNotFoundException;
import com.cleany.transfer.TransferAssignmentConflictException;
import com.cleany.transfer.TransferDriverLinkException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage())
        );
        return response(HttpStatus.BAD_REQUEST, "validation_failed", "Request validation failed", fieldErrors);
    }

    @ExceptionHandler({ConstraintViolationException.class, HttpMessageNotReadableException.class})
    ResponseEntity<ApiError> handleMalformedRequest(Exception exception) {
        return response(HttpStatus.BAD_REQUEST, "invalid_request", "Request body is invalid", Collections.emptyMap());
    }

    @ExceptionHandler(BookingDateNotAvailableException.class)
    ResponseEntity<ApiError> handleBookingDate(BookingDateNotAvailableException exception) {
        return response(
                HttpStatus.BAD_REQUEST,
                "booking_date_unavailable",
                exception.getMessage(),
                Map.of("requestedDate", exception.getMessage())
        );
    }

    @ExceptionHandler(InvalidPhoneNumberException.class)
    ResponseEntity<ApiError> handleInvalidPhoneNumber(InvalidPhoneNumberException exception) {
        return response(
                HttpStatus.BAD_REQUEST,
                "invalid_phone_number",
                exception.getMessage(),
                Map.of("phone", "must be a valid international phone number with country code")
        );
    }

    @ExceptionHandler(ReferralNotApplicableException.class)
    ResponseEntity<ApiError> handleReferralNotApplicable(ReferralNotApplicableException exception) {
        return response(
                HttpStatus.BAD_REQUEST,
                "referral_not_applicable",
                exception.getMessage(),
                Map.of("referralCode", exception.getMessage())
        );
    }

    @ExceptionHandler(InvalidOnsiteIssueException.class)
    ResponseEntity<ApiError> handleInvalidOnsiteIssue(InvalidOnsiteIssueException exception) {
        return response(
                HttpStatus.BAD_REQUEST,
                "invalid_onsite_issue",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(OrderNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "order_not_found", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(RentalPropertyNotFoundException.class)
    ResponseEntity<ApiError> handleRentalPropertyNotFound(RentalPropertyNotFoundException exception) {
        return response(
                HttpStatus.NOT_FOUND,
                "rental_property_not_found",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(RentalPropertyMediaNotFoundException.class)
    ResponseEntity<ApiError> handleRentalPropertyMediaNotFound(
            RentalPropertyMediaNotFoundException exception
    ) {
        return response(
                HttpStatus.NOT_FOUND,
                "rental_property_media_not_found",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(InvalidRentalPropertyMediaException.class)
    ResponseEntity<ApiError> handleInvalidRentalPropertyMedia(
            InvalidRentalPropertyMediaException exception
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "invalid_rental_property_media",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(RentalPropertyCannotBePublishedException.class)
    ResponseEntity<ApiError> handleRentalPropertyCannotBePublished(
            RentalPropertyCannotBePublishedException exception
    ) {
        return response(
                HttpStatus.CONFLICT,
                "rental_property_cannot_be_published",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(RentalPropertyCannotBeDeletedException.class)
    ResponseEntity<ApiError> handleRentalPropertyCannotBeDeleted(
            RentalPropertyCannotBeDeletedException exception
    ) {
        return response(
                HttpStatus.CONFLICT,
                "rental_property_cannot_be_deleted",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(RentalPropertyCannotBeUnpublishedException.class)
    ResponseEntity<ApiError> handleRentalPropertyCannotBeUnpublished(
            RentalPropertyCannotBeUnpublishedException exception
    ) {
        return response(
                HttpStatus.CONFLICT,
                "rental_property_cannot_be_unpublished",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(RentalBookingNotFoundException.class)
    ResponseEntity<ApiError> handleRentalBookingNotFound(RentalBookingNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "rental_booking_not_found", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(RentalOccupancyNotFoundException.class)
    ResponseEntity<ApiError> handleRentalOccupancyNotFound(
            RentalOccupancyNotFoundException exception
    ) {
        return response(
                HttpStatus.NOT_FOUND,
                "rental_occupancy_not_found",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(InvalidRentalDateRangeException.class)
    ResponseEntity<ApiError> handleInvalidRentalDateRange(InvalidRentalDateRangeException exception) {
        return response(
                HttpStatus.BAD_REQUEST,
                "invalid_rental_date_range",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(RentalMinimumStayNotMetException.class)
    ResponseEntity<ApiError> handleRentalMinimumStay(RentalMinimumStayNotMetException exception) {
        return response(
                HttpStatus.BAD_REQUEST,
                "rental_min_stay_not_met",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(RentalMaximumStayExceededException.class)
    ResponseEntity<ApiError> handleRentalMaximumStay(RentalMaximumStayExceededException exception) {
        return response(
                HttpStatus.BAD_REQUEST,
                "rental_max_stay_exceeded",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(RentalBookingHorizonExceededException.class)
    ResponseEntity<ApiError> handleRentalBookingHorizon(
            RentalBookingHorizonExceededException exception
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "rental_booking_horizon_exceeded",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler({InvalidRentalBookingException.class, InvalidRentalOccupancyException.class})
    ResponseEntity<ApiError> handleInvalidRentalOperation(RuntimeException exception) {
        String code = exception instanceof InvalidRentalBookingException
                ? "invalid_rental_booking"
                : "invalid_rental_occupancy";
        return response(HttpStatus.BAD_REQUEST, code, exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(RentalPropertyNotAvailableException.class)
    ResponseEntity<ApiError> handleRentalPropertyNotAvailable(
            RentalPropertyNotAvailableException exception
    ) {
        return response(
                HttpStatus.CONFLICT,
                "rental_property_not_available",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(RentalDatesNotAvailableException.class)
    ResponseEntity<ApiError> handleRentalDatesNotAvailable(RentalDatesNotAvailableException exception) {
        return response(HttpStatus.CONFLICT, "dates_not_available", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(RentalActiveBookingLimitExceededException.class)
    ResponseEntity<ApiError> handleRentalBookingLimit(
            RentalActiveBookingLimitExceededException exception
    ) {
        return response(
                HttpStatus.CONFLICT,
                "rental_active_booking_limit_exceeded",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(RentalBookingCannotBeCancelledException.class)
    ResponseEntity<ApiError> handleRentalBookingCannotBeCancelled(
            RentalBookingCannotBeCancelledException exception
    ) {
        return response(
                HttpStatus.CONFLICT,
                "rental_booking_cannot_be_cancelled",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(RentalBookingCannotBeCompletedException.class)
    ResponseEntity<ApiError> handleRentalBookingCannotBeCompleted(
            RentalBookingCannotBeCompletedException exception
    ) {
        return response(
                HttpStatus.CONFLICT,
                "rental_booking_cannot_be_completed",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(CustomerAuthenticationRequiredException.class)
    ResponseEntity<ApiError> handleAuthentication(CustomerAuthenticationRequiredException exception) {
        return response(HttpStatus.UNAUTHORIZED, "authentication_required", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(TelegramWebhookAuthenticationException.class)
    ResponseEntity<ApiError> handleWebhookAuthentication(TelegramWebhookAuthenticationException exception) {
        return response(HttpStatus.UNAUTHORIZED, "invalid_webhook_secret", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(CleanerNotAuthorizedException.class)
    ResponseEntity<ApiError> handleCleanerAuthorization(CleanerNotAuthorizedException exception) {
        return response(HttpStatus.FORBIDDEN, "cleaner_not_authorized", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(AdminNotAuthorizedException.class)
    ResponseEntity<ApiError> handleAdminAuthorization(AdminNotAuthorizedException exception) {
        return response(HttpStatus.FORBIDDEN, "admin_not_authorized", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler({InvalidOrderStateException.class, OrderClaimConflictException.class})
    ResponseEntity<ApiError> handleConflict(RuntimeException exception) {
        return response(HttpStatus.CONFLICT, "order_state_conflict", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(CleaningReportExpiredException.class)
    ResponseEntity<ApiError> handleCleaningReportExpired(CleaningReportExpiredException exception) {
        return response(HttpStatus.GONE, "cleaning_report_expired", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(AccountLinkProviderException.class)
    ResponseEntity<ApiError> handleAccountLinkProvider(AccountLinkProviderException exception) {
        return response(HttpStatus.FORBIDDEN, "account_link_provider_required", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(AccountLinkConflictException.class)
    ResponseEntity<ApiError> handleAccountLinkConflict(AccountLinkConflictException exception) {
        return response(HttpStatus.CONFLICT, "account_link_conflict", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(AccountLinkTokenExpiredException.class)
    ResponseEntity<ApiError> handleAccountLinkExpired(AccountLinkTokenExpiredException exception) {
        return response(HttpStatus.GONE, "account_link_expired", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(AccountLinkTokenConsumedException.class)
    ResponseEntity<ApiError> handleAccountLinkConsumed(AccountLinkTokenConsumedException exception) {
        return response(HttpStatus.CONFLICT, "account_link_consumed", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(AccountLinkTokenInvalidException.class)
    ResponseEntity<ApiError> handleAccountLinkInvalid(AccountLinkTokenInvalidException exception) {
        return response(HttpStatus.NOT_FOUND, "account_link_invalid", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(TelegramIdentityNotLinkedException.class)
    ResponseEntity<ApiError> handleTelegramIdentityNotLinked(TelegramIdentityNotLinkedException exception) {
        return response(HttpStatus.CONFLICT, "telegram_identity_not_linked", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(CustomerNotificationNotFoundException.class)
    ResponseEntity<ApiError> handleCustomerNotificationNotFound(
            CustomerNotificationNotFoundException exception
    ) {
        return response(HttpStatus.NOT_FOUND, "customer_notification_not_found", exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(AcquisitionCampaignNotFoundException.class)
    ResponseEntity<ApiError> handleAcquisitionCampaignNotFound(
            AcquisitionCampaignNotFoundException exception
    ) {
        return response(
                HttpStatus.NOT_FOUND,
                "acquisition_campaign_not_found",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(InvalidAcquisitionCampaignException.class)
    ResponseEntity<ApiError> handleInvalidAcquisitionCampaign(
            InvalidAcquisitionCampaignException exception
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "invalid_acquisition_campaign",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(InvalidAnalyticsPeriodException.class)
    ResponseEntity<ApiError> handleInvalidAnalyticsPeriod(InvalidAnalyticsPeriodException exception) {
        return response(
                HttpStatus.BAD_REQUEST,
                "invalid_analytics_period",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(PlatformServiceNotAvailableException.class)
    ResponseEntity<ApiError> handlePlatformServiceNotAvailable(
            PlatformServiceNotAvailableException exception
    ) {
        return response(
                HttpStatus.SERVICE_UNAVAILABLE,
                "service_not_available",
                "The requested service is not currently available",
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(TransferBookingNotFoundException.class)
    ResponseEntity<ApiError> handleTransferBookingNotFound(
            TransferBookingNotFoundException exception
    ) {
        return response(
                HttpStatus.NOT_FOUND,
                "transfer_booking_not_found",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(TransferConfigurationNotFoundException.class)
    ResponseEntity<ApiError> handleTransferConfigurationNotFound(
            TransferConfigurationNotFoundException exception
    ) {
        return response(
                HttpStatus.NOT_FOUND,
                "transfer_configuration_not_found",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(InvalidTransferBookingException.class)
    ResponseEntity<ApiError> handleInvalidTransferBooking(
            InvalidTransferBookingException exception
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "invalid_transfer_booking",
                exception.getMessage(),
                Collections.emptyMap()
        );
    }

    @ExceptionHandler({
            TransferBookingStateException.class,
            TransferConfigurationUnavailableException.class,
            TransferAssignmentConflictException.class,
            TransferDriverLinkException.class
    })
    ResponseEntity<ApiError> handleTransferConflict(RuntimeException exception) {
        String code = switch (exception) {
            case TransferBookingStateException ignored -> "transfer_booking_state_conflict";
            case TransferAssignmentConflictException ignored -> "transfer_assignment_conflict";
            case TransferDriverLinkException ignored -> "transfer_driver_link_conflict";
            default -> "transfer_configuration_unavailable";
        };
        return response(HttpStatus.CONFLICT, code, exception.getMessage(), Collections.emptyMap());
    }

    @ExceptionHandler(RentalCleaningBenefitNotApplicableException.class)
    ResponseEntity<ApiError> handleRentalCleaningBenefitNotApplicable(
            RentalCleaningBenefitNotApplicableException exception
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "rental_cleaning_benefit_not_applicable",
                exception.getMessage(),
                Map.of("rentalCleaningPromoCode", exception.getMessage())
        );
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    ResponseEntity<ApiError> handleConcurrentUpdate(OptimisticLockingFailureException exception) {
        return response(
                HttpStatus.CONFLICT,
                "concurrent_update_conflict",
                "The resource was changed by another request",
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiError> handleNoResource(NoResourceFoundException exception) {
        return response(
                HttpStatus.NOT_FOUND,
                "resource_not_found",
                "The requested resource was not found",
                Collections.emptyMap()
        );
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        log.error("Unhandled backend error", exception);
        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "internal_error",
                "An unexpected error occurred",
                Collections.emptyMap()
        );
    }

    private ResponseEntity<ApiError> response(
            HttpStatus status,
            String code,
            String message,
            Map<String, String> fieldErrors
    ) {
        return ResponseEntity.status(status).body(new ApiError(
                clock.instant(),
                status.value(),
                code,
                message,
                fieldErrors
        ));
    }
}
