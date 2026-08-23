package com.cleany.rental;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/rental/bookings")
@RequiredArgsConstructor
public class AdminRentalBookingController {

    private final AdminRentalBookingService bookingService;

    @GetMapping
    public List<AdminRentalBookingResponse> getBookings(
            @RequestParam(required = false) RentalBookingStatus status,
            @RequestParam(required = false) Long propertyId,
            @RequestParam(defaultValue = "ALL") RentalBookingTimeFilter time
    ) {
        return bookingService.getBookings(status, propertyId, time);
    }

    @GetMapping("/{bookingId}")
    public AdminRentalBookingResponse getBooking(@PathVariable long bookingId) {
        return bookingService.getBooking(bookingId);
    }

    @PostMapping("/{bookingId}/cancel")
    public AdminRentalBookingResponse cancel(
            @PathVariable long bookingId,
            @Valid @RequestBody AdminCancelRentalBookingRequest request
    ) {
        return bookingService.cancel(bookingId, request);
    }

    @PostMapping("/{bookingId}/complete")
    public AdminRentalBookingResponse complete(@PathVariable long bookingId) {
        return bookingService.complete(bookingId);
    }
}
