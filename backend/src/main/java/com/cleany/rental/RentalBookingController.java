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

import com.cleany.crossservice.rentalcleaning.RentalCleaningContextResponse;
import com.cleany.crossservice.rentalcleaning.RentalCleaningContextService;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextResponse;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextService;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextType;
import com.cleany.crossservice.rentaltransfer.RentalTransferPrefillResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(RentalBookingController.BASE_PATH)
@RequiredArgsConstructor
public class RentalBookingController {

    static final String BASE_PATH = "/api/v1/rental/bookings";

    private final RentalBookingService bookingService;
    private final RentalCleaningContextService cleaningContextService;
    private final RentalTransferContextService transferContextService;

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

    @GetMapping("/{bookingId}/cleaning-context")
    public RentalCleaningContextResponse getCleaningContext(@PathVariable long bookingId) {
        return cleaningContextService.currentCustomerContext(bookingId);
    }

    @GetMapping("/{bookingId}/transfer-context")
    public RentalTransferContextResponse getTransferContext(@PathVariable long bookingId) {
        return transferContextService.currentCustomerContext(bookingId);
    }

    @PostMapping("/{bookingId}/transfer-context/{context}/shown")
    public ResponseEntity<Void> recordTransferContextShown(
            @PathVariable long bookingId,
            @PathVariable RentalTransferContextType context
    ) {
        transferContextService.recordShown(bookingId, context);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookingId}/transfer-context/{context}/prefill")
    public RentalTransferPrefillResponse transferPrefill(
            @PathVariable long bookingId,
            @PathVariable RentalTransferContextType context
    ) {
        return transferContextService.prefill(bookingId, context);
    }

    @PostMapping("/{bookingId}/cancel")
    public RentalBookingResponse cancel(@PathVariable long bookingId) {
        return bookingService.cancelCurrentCustomerBooking(bookingId);
    }
}
