package com.cleany.configuration;

import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cleany.order.BookingDateNotAvailableException;
import com.cleany.admin.AdminNotAuthorizedException;
import com.cleany.order.CleanerNotAuthorizedException;
import com.cleany.order.InvalidPhoneNumberException;
import com.cleany.order.InvalidOrderStateException;
import com.cleany.order.OrderClaimConflictException;
import com.cleany.order.OrderNotFoundException;
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
        return response(HttpStatus.BAD_REQUEST, "booking_date_unavailable", exception.getMessage(), Map.of());
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

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(OrderNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "order_not_found", exception.getMessage(), Map.of());
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
