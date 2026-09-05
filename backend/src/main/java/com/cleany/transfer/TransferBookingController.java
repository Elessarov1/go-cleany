package com.cleany.transfer;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(TransferBookingController.BASE_PATH)
@RequiredArgsConstructor
public class TransferBookingController {

    static final String BASE_PATH = "/api/v1/transfer";

    private final TransferBookingService bookingService;

    @GetMapping("/configuration")
    public TransferConfigurationResponse configuration() {
        return bookingService.configuration();
    }

    @PostMapping("/bookings")
    public ResponseEntity<TransferBookingResponse> create(
            @Valid @RequestBody CreateTransferBookingRequest request
    ) {
        TransferBookingResponse booking = bookingService.create(request);
        return ResponseEntity.created(URI.create(BASE_PATH + "/bookings/" + booking.id()))
                .body(booking);
    }

    @PostMapping("/quote")
    public TransferQuoteResponse quote(@Valid @RequestBody TransferQuoteRequest request) {
        return bookingService.quote(request);
    }

    @GetMapping("/bookings")
    public List<TransferBookingResponse> bookings() {
        return bookingService.currentCustomerBookings();
    }

    @GetMapping("/bookings/{bookingId}")
    public TransferBookingResponse booking(@PathVariable long bookingId) {
        return bookingService.currentCustomerBooking(bookingId);
    }

    @PostMapping("/bookings/{bookingId}/repeat-shown")
    public ResponseEntity<Void> recordRepeatShown(@PathVariable long bookingId) {
        bookingService.recordRepeatShown(bookingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bookings/{bookingId}/repeat-prefill")
    public TransferRepeatPrefillResponse repeatPrefill(@PathVariable long bookingId) {
        return bookingService.repeatPrefill(bookingId);
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public TransferBookingResponse cancel(@PathVariable long bookingId) {
        return bookingService.cancelCurrentCustomerBooking(bookingId);
    }
}
