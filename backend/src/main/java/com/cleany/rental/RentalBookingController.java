package com.cleany.rental;

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
@RequestMapping(RentalBookingController.BASE_PATH)
@RequiredArgsConstructor
public class RentalBookingController {

    static final String BASE_PATH = "/api/v1/rental/bookings";

    private final RentalBookingService bookingService;

    @PostMapping("/quote")
    public RentalBookingQuoteResponse quote(
            @Valid @RequestBody RentalBookingQuoteRequest request
    ) {
        return bookingService.quote(request);
    }

    @PostMapping
    public ResponseEntity<RentalBookingResponse> create(
            @Valid @RequestBody CreateRentalBookingRequest request
    ) {
        RentalBookingResponse booking = bookingService.create(request);
        return ResponseEntity
                .created(URI.create(BASE_PATH + "/" + booking.id()))
                .body(booking);
    }

    @GetMapping
    public List<RentalBookingResponse> getBookings() {
        return bookingService.currentCustomerBookings();
    }

    @GetMapping("/{bookingId}")
    public RentalBookingResponse getBooking(@PathVariable long bookingId) {
        return bookingService.currentCustomerBooking(bookingId);
    }

    @PostMapping("/{bookingId}/cancel")
    public RentalBookingResponse cancel(@PathVariable long bookingId) {
        return bookingService.cancelCurrentCustomerBooking(bookingId);
    }
}
