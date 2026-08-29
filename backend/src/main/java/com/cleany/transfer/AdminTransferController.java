package com.cleany.transfer;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/transfer")
@RequiredArgsConstructor
public class AdminTransferController {

    private final AdminTransferService transferService;
    private final TransferDriverLinkService driverLinkService;

    @GetMapping("/airports")
    public List<AdminTransferAirportResponse> airports() {
        return transferService.airports();
    }

    @PostMapping("/airports")
    public ResponseEntity<AdminTransferAirportResponse> createAirport(
            @Valid @RequestBody CreateTransferAirportRequest request
    ) {
        AdminTransferAirportResponse airport = transferService.createAirport(request);
        return ResponseEntity.created(URI.create("/api/v1/admin/transfer/airports/" + airport.id()))
                .body(airport);
    }

    @PutMapping("/airports/{airportId}")
    public AdminTransferAirportResponse updateAirport(
            @PathVariable long airportId,
            @Valid @RequestBody UpdateTransferAirportRequest request
    ) {
        return transferService.updateAirport(airportId, request);
    }

    @GetMapping("/vehicles")
    public List<AdminTransferVehicleTypeResponse> vehicles() {
        return transferService.vehicles();
    }

    @PostMapping("/vehicles")
    public ResponseEntity<AdminTransferVehicleTypeResponse> createVehicle(
            @Valid @RequestBody CreateTransferVehicleTypeRequest request
    ) {
        AdminTransferVehicleTypeResponse vehicle = transferService.createVehicle(request);
        return ResponseEntity.created(URI.create("/api/v1/admin/transfer/vehicles/" + vehicle.id()))
                .body(vehicle);
    }

    @PutMapping("/vehicles/{vehicleId}")
    public AdminTransferVehicleTypeResponse updateVehicle(
            @PathVariable long vehicleId,
            @Valid @RequestBody UpdateTransferVehicleTypeRequest request
    ) {
        return transferService.updateVehicle(vehicleId, request);
    }

    @GetMapping("/prices")
    public List<AdminTransferPriceResponse> prices() {
        return transferService.prices();
    }

    @PutMapping("/prices")
    public AdminTransferPriceResponse upsertPrice(
            @Valid @RequestBody UpsertTransferPriceRequest request
    ) {
        return transferService.upsertPrice(request);
    }

    @GetMapping("/drivers")
    public List<AdminTransferDriverResponse> drivers() {
        return transferService.drivers();
    }

    @PostMapping("/drivers")
    public ResponseEntity<AdminTransferDriverResponse> createDriver(
            @Valid @RequestBody CreateTransferDriverRequest request
    ) {
        AdminTransferDriverResponse driver = transferService.createDriver(request);
        return ResponseEntity.created(URI.create("/api/v1/admin/transfer/drivers/" + driver.id()))
                .body(driver);
    }

    @PutMapping("/drivers/{driverId}")
    public AdminTransferDriverResponse updateDriver(
            @PathVariable long driverId,
            @Valid @RequestBody CreateTransferDriverRequest request
    ) {
        return transferService.updateDriver(driverId, request);
    }

    @PostMapping("/drivers/{driverId}/telegram-link")
    public TransferDriverLinkResponse createDriverTelegramLink(@PathVariable long driverId) {
        return driverLinkService.createLink(driverId);
    }

    @GetMapping("/bookings")
    public List<TransferBookingResponse> bookings(
            @RequestParam(required = false) TransferBookingStatus status,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) Long airportId
    ) {
        return transferService.bookings(status, date, airportId);
    }

    @GetMapping("/bookings/{bookingId}")
    public TransferBookingResponse booking(@PathVariable long bookingId) {
        return transferService.booking(bookingId);
    }

    @PostMapping("/bookings/{bookingId}/assign")
    public TransferBookingResponse assign(
            @PathVariable long bookingId,
            @Valid @RequestBody AssignTransferDriverRequest request
    ) {
        return transferService.assign(bookingId, request.driverId());
    }

    @PostMapping("/bookings/{bookingId}/reject")
    public TransferBookingResponse reject(
            @PathVariable long bookingId,
            @Valid @RequestBody TransferStatusReasonRequest request
    ) {
        return transferService.reject(bookingId, request.reason());
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public TransferBookingResponse cancel(
            @PathVariable long bookingId,
            @Valid @RequestBody TransferStatusReasonRequest request
    ) {
        return transferService.cancel(bookingId, request.reason());
    }

    @PostMapping("/bookings/{bookingId}/complete")
    public TransferBookingResponse complete(@PathVariable long bookingId) {
        return transferService.complete(bookingId);
    }
}
