package com.cleany.configuration;

import java.time.Clock;
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

import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitNotApplicableException;
import com.cleany.order.BookingDateNotAvailableException;
import com.cleany.admin.AdminNotAuthorizedException;
import com.cleany.order.CleanerNotAuthorizedException;
import com.cleany.order.InvalidPhoneNumberException;
import com.cleany.order.InvalidOrderStateException;
import com.cleany.order.InvalidOnsiteIssueException;
import com.cleany.order.OrderClaimConflictException;
import com.cleany.order.OrderNotFoundException;
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
import com.cleany.telegram.CustomerAuthenticationRequiredException;
import com.cleany.telegram.bot.TelegramWebhookAuthenticationException;

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
        return response(HttpStatus.BAD_REQUEST, "invalid_request", "Request body is invalid", Map.of());
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
                Map.of()
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(OrderNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "order_not_found", exception.getMessage(), Map.of());
    }

    @ExceptionHandler(RentalPropertyNotFoundException.class)
    ResponseEntity<ApiError> handleRentalPropertyNotFound(RentalPropertyNotFoundException exception) {
        return response(
                HttpStatus.NOT_FOUND,
                "rental_property_not_found",
                exception.getMessage(),
                Map.of()
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
                Map.of()
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
                Map.of()
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
                Map.of()
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
                Map.of()
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
                Map.of()
        );
    }

    @ExceptionHandler(RentalBookingNotFoundException.class)
    ResponseEntity<ApiError> handleRentalBookingNotFound(RentalBookingNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "rental_booking_not_found", exception.getMessage(), Map.of());
    }

    @ExceptionHandler(RentalOccupancyNotFoundException.class)
    ResponseEntity<ApiError> handleRentalOccupancyNotFound(
            RentalOccupancyNotFoundException exception
    ) {
        return response(
                HttpStatus.NOT_FOUND,
                "rental_occupancy_not_found",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(InvalidRentalDateRangeException.class)
    ResponseEntity<ApiError> handleInvalidRentalDateRange(InvalidRentalDateRangeException exception) {
        return response(
                HttpStatus.BAD_REQUEST,
                "invalid_rental_date_range",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(RentalMinimumStayNotMetException.class)
    ResponseEntity<ApiError> handleRentalMinimumStay(RentalMinimumStayNotMetException exception) {
        return response(
                HttpStatus.BAD_REQUEST,
                "rental_min_stay_not_met",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(RentalMaximumStayExceededException.class)
    ResponseEntity<ApiError> handleRentalMaximumStay(RentalMaximumStayExceededException exception) {
        return response(
                HttpStatus.BAD_REQUEST,
                "rental_max_stay_exceeded",
                exception.getMessage(),
                Map.of()
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
                Map.of()
        );
    }

    @ExceptionHandler({InvalidRentalBookingException.class, InvalidRentalOccupancyException.class})
    ResponseEntity<ApiError> handleInvalidRentalOperation(RuntimeException exception) {
        String code = exception instanceof InvalidRentalBookingException
                ? "invalid_rental_booking"
                : "invalid_rental_occupancy";
        return response(HttpStatus.BAD_REQUEST, code, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(RentalPropertyNotAvailableException.class)
    ResponseEntity<ApiError> handleRentalPropertyNotAvailable(
            RentalPropertyNotAvailableException exception
    ) {
        return response(
                HttpStatus.CONFLICT,
                "rental_property_not_available",
                exception.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(RentalDatesNotAvailableException.class)
    ResponseEntity<ApiError> handleRentalDatesNotAvailable(RentalDatesNotAvailableException exception) {
        return response(HttpStatus.CONFLICT, "dates_not_available", exception.getMessage(), Map.of());
    }

    @ExceptionHandler(RentalActiveBookingLimitExceededException.class)
    ResponseEntity<ApiError> handleRentalBookingLimit(
            RentalActiveBookingLimitExceededException exception
    ) {
        return response(
                HttpStatus.CONFLICT,
                "rental_active_booking_limit_exceeded",
                exception.getMessage(),
                Map.of()
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
                Map.of()
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
                Map.of()
        );
    }

    @ExceptionHandler(CustomerAuthenticationRequiredException.class)
    ResponseEntity<ApiError> handleAuthentication(CustomerAuthenticationRequiredException exception) {
        return response(HttpStatus.UNAUTHORIZED, "authentication_required", exception.getMessage(), Map.of());
    }

    @ExceptionHandler(TelegramWebhookAuthenticationException.class)
    ResponseEntity<ApiError> handleWebhookAuthentication(TelegramWebhookAuthenticationException exception) {
        return response(HttpStatus.UNAUTHORIZED, "invalid_webhook_secret", exception.getMessage(), Map.of());
    }

    @ExceptionHandler(CleanerNotAuthorizedException.class)
    ResponseEntity<ApiError> handleCleanerAuthorization(CleanerNotAuthorizedException exception) {
        return response(HttpStatus.FORBIDDEN, "cleaner_not_authorized", exception.getMessage(), Map.of());
    }

    @ExceptionHandler(AdminNotAuthorizedException.class)
    ResponseEntity<ApiError> handleAdminAuthorization(AdminNotAuthorizedException exception) {
        return response(HttpStatus.FORBIDDEN, "admin_not_authorized", exception.getMessage(), Map.of());
    }

    @ExceptionHandler({InvalidOrderStateException.class, OrderClaimConflictException.class})
    ResponseEntity<ApiError> handleConflict(RuntimeException exception) {
        return response(HttpStatus.CONFLICT, "order_state_conflict", exception.getMessage(), Map.of());
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
                Map.of()
        );
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        log.error("Unhandled backend error", exception);
        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "internal_error",
                "An unexpected error occurred",
                Map.of()
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
