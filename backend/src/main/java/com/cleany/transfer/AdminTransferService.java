package com.cleany.transfer;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import com.cleany.admin.AdminAccessService;
import com.cleany.order.PhoneNumberNormalizer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminTransferService {

    private final AdminAccessService accessService;
    private final TransferAirportRepository airportRepository;
    private final TransferVehicleTypeRepository vehicleRepository;
    private final TransferPriceRepository priceRepository;
    private final TransferDriverRepository driverRepository;
    private final TransferBookingRepository bookingRepository;
    private final TransferBookingPolicy bookingPolicy;
    private final PhoneNumberNormalizer phoneNumberNormalizer;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<AdminTransferAirportResponse> airports() {
        accessService.requireCurrentAdmin();
        return airportRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .map(AdminTransferAirportResponse::from)
                .toList();
    }

    @Transactional
    public AdminTransferAirportResponse createAirport(CreateTransferAirportRequest request) {
        accessService.requireCurrentAdmin();
        return AdminTransferAirportResponse.from(airportRepository.saveAndFlush(new TransferAirport(
                request.code(), request.nameRu(), request.nameEn(), request.enabled(),
                request.sortOrder(), clock.instant()
        )));
    }

    @Transactional
    public AdminTransferAirportResponse updateAirport(long airportId, UpdateTransferAirportRequest request) {
        accessService.requireCurrentAdmin();
        TransferAirport airport = requireAirport(airportId);
        airport.update(
                request.nameRu(), request.nameEn(), request.enabled(), request.sortOrder(), clock.instant()
        );
        return AdminTransferAirportResponse.from(airport);
    }

    @Transactional(readOnly = true)
    public List<AdminTransferVehicleTypeResponse> vehicles() {
        accessService.requireCurrentAdmin();
        return vehicleRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .map(AdminTransferVehicleTypeResponse::from)
                .toList();
    }

    @Transactional
    public AdminTransferVehicleTypeResponse createVehicle(CreateTransferVehicleTypeRequest request) {
        accessService.requireCurrentAdmin();
        return AdminTransferVehicleTypeResponse.from(vehicleRepository.saveAndFlush(
                new TransferVehicleType(
                        request.code(), request.nameRu(), request.nameEn(), request.maxPassengers(),
                        request.maxLuggage(), request.enabled(), request.sortOrder(), clock.instant()
                )
        ));
    }

    @Transactional
    public AdminTransferVehicleTypeResponse updateVehicle(
            long vehicleId,
            UpdateTransferVehicleTypeRequest request
    ) {
        accessService.requireCurrentAdmin();
        TransferVehicleType vehicle = requireVehicle(vehicleId);
        vehicle.update(
                request.nameRu(), request.nameEn(), request.maxPassengers(), request.maxLuggage(),
                request.enabled(), request.sortOrder(), clock.instant()
        );
        return AdminTransferVehicleTypeResponse.from(vehicle);
    }

    @Transactional(readOnly = true)
    public List<AdminTransferPriceResponse> prices() {
        accessService.requireCurrentAdmin();
        return priceRepository.findAllByOrderByAirport_SortOrderAscVehicleType_SortOrderAscDirectionAsc()
                .stream()
                .map(AdminTransferPriceResponse::from)
                .toList();
    }

    @Transactional
    public AdminTransferPriceResponse upsertPrice(UpsertTransferPriceRequest request) {
        accessService.requireCurrentAdmin();
        TransferAirport airport = requireAirport(request.airportId());
        TransferVehicleType vehicle = requireVehicle(request.vehicleTypeId());
        TransferPrice price = priceRepository.findByAirport_IdAndVehicleType_IdAndDirection(
                        airport.getId(), vehicle.getId(), request.direction()
                )
                .orElseGet(() -> new TransferPrice(
                        airport, vehicle, request.direction(), request.amount(), request.currency(),
                        request.enabled(), clock.instant()
                ));
        if (price.getId() != null) {
            price.update(request.amount(), request.currency(), request.enabled(), clock.instant());
        }
        return AdminTransferPriceResponse.from(priceRepository.saveAndFlush(price));
    }

    @Transactional(readOnly = true)
    public List<AdminTransferDriverResponse> drivers() {
        accessService.requireCurrentAdmin();
        return driverRepository.findAllByOrderByNameAscIdAsc().stream()
                .map(AdminTransferDriverResponse::from)
                .toList();
    }

    @Transactional
    public AdminTransferDriverResponse createDriver(CreateTransferDriverRequest request) {
        accessService.requireCurrentAdmin();
        return AdminTransferDriverResponse.from(driverRepository.saveAndFlush(new TransferDriver(
                request.name(), phoneNumberNormalizer.normalize(request.phone()), request.enabled(),
                request.telegramUserId(), clock.instant()
        )));
    }

    @Transactional
    public AdminTransferDriverResponse updateDriver(
            long driverId,
            CreateTransferDriverRequest request
    ) {
        accessService.requireCurrentAdmin();
        TransferDriver driver = requireDriver(driverId);
        driver.update(
                request.name(), phoneNumberNormalizer.normalize(request.phone()), request.enabled(),
                request.telegramUserId(), clock.instant()
        );
        return AdminTransferDriverResponse.from(driver);
    }

    @Transactional(readOnly = true)
    public List<TransferBookingResponse> bookings(
            TransferBookingStatus status,
            LocalDate date,
            Long airportId
    ) {
        accessService.requireCurrentAdmin();
        return bookingRepository.findAllByOrderByPickupDateDescPickupTimeDescIdDesc().stream()
                .filter(booking -> status == null || booking.getStatus() == status)
                .filter(booking -> date == null || booking.getPickupDate().equals(date))
                .filter(booking -> airportId == null || booking.getAirport().getId().equals(airportId))
                .map(TransferBookingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransferBookingResponse booking(long bookingId) {
        accessService.requireCurrentAdmin();
        TransferBooking booking = requireBooking(bookingId);
        publishStatus(booking, TransferBookingCustomerEvent.Type.CONFIRMED);
        return TransferBookingResponse.from(booking);
    }

    @Transactional
    public TransferBookingResponse assign(long bookingId, long driverId) {
        accessService.requireCurrentAdmin();
        requireDriverEnabled(driverId);
        if (bookingRepository.assignRequestedBooking(bookingId, driverId, clock.instant()) != 1) {
            throw new TransferAssignmentConflictException(bookingId);
        }
        return TransferBookingResponse.from(requireBooking(bookingId));
    }

    @Transactional
    public TransferBookingResponse reject(long bookingId, String reason) {
        accessService.requireCurrentAdmin();
        TransferBooking booking = requireBookingForUpdate(bookingId);
        booking.reject(reason, clock.instant());
        publishStatus(booking, TransferBookingCustomerEvent.Type.REJECTED);
        return TransferBookingResponse.from(booking);
    }

    @Transactional
    public TransferBookingResponse cancel(long bookingId, String reason) {
        accessService.requireCurrentAdmin();
        TransferBooking booking = requireBookingForUpdate(bookingId);
        booking.cancelByAdmin(reason, clock.instant());
        publishStatus(booking, TransferBookingCustomerEvent.Type.CANCELLED);
        return TransferBookingResponse.from(booking);
    }

    @Transactional
    public TransferBookingResponse complete(long bookingId) {
        accessService.requireCurrentAdmin();
        TransferBooking booking = requireBookingForUpdate(bookingId);
        booking.complete(bookingPolicy.hasStarted(booking), clock.instant());
        publishStatus(booking, TransferBookingCustomerEvent.Type.COMPLETED);
        return TransferBookingResponse.from(booking);
    }

    private void publishStatus(TransferBooking booking, TransferBookingCustomerEvent.Type type) {
        eventPublisher.publishEvent(new TransferBookingCustomerEvent(
                booking.getId(), booking.getCustomerId(), booking.getCommunicationIdentityId(), type
        ));
    }

    private TransferAirport requireAirport(long airportId) {
        return airportRepository.findById(airportId)
                .orElseThrow(() -> new TransferConfigurationNotFoundException("airport", airportId));
    }

    private TransferVehicleType requireVehicle(long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new TransferConfigurationNotFoundException("vehicle type", vehicleId));
    }

    private TransferDriver requireDriver(long driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new TransferConfigurationNotFoundException("driver", driverId));
    }

    private TransferDriver requireDriverEnabled(long driverId) {
        return driverRepository.findByIdAndEnabledTrue(driverId)
                .orElseThrow(() -> new TransferConfigurationUnavailableException("driver"));
    }

    private TransferBooking requireBooking(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new TransferBookingNotFoundException(bookingId));
    }

    private TransferBooking requireBookingForUpdate(long bookingId) {
        return bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new TransferBookingNotFoundException(bookingId));
    }
}
