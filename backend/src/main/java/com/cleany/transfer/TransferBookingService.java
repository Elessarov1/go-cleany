package com.cleany.transfer;

import java.time.Clock;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import com.cleany.analytics.CustomerAttributionService;
import com.cleany.catalog.PlatformService;
import com.cleany.catalog.PlatformServiceAccessService;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.order.PhoneNumberNormalizer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferBookingService {

    private final TransferAirportRepository airportRepository;
    private final TransferVehicleTypeRepository vehicleRepository;
    private final TransferPriceRepository priceRepository;
    private final TransferBookingRepository bookingRepository;
    private final TransferBookingPolicy bookingPolicy;
    private final PlatformServiceAccessService platformServiceAccessService;
    private final CustomerAccountService customerAccountService;
    private final PhoneNumberNormalizer phoneNumberNormalizer;
    private final CustomerAttributionService customerAttributionService;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Transactional(readOnly = true)
    public TransferConfigurationResponse configuration() {
        return configuration(customerAccountService.currentCustomer());
    }

    @Transactional(readOnly = true)
    public TransferConfigurationResponse configuration(CurrentCustomer customer) {
        requireCustomerFlow(customer);
        return new TransferConfigurationResponse(
                bookingPolicy.earliestBookingDate(),
                bookingPolicy.latestBookingDate(),
                bookingPolicy.timeSlotMinutes(),
                airportRepository.findAllByEnabledTrueOrderBySortOrderAscIdAsc().stream()
                        .map(TransferAirportResponse::from)
                        .toList(),
                vehicleRepository.findAllByEnabledTrueOrderBySortOrderAscIdAsc().stream()
                        .map(TransferVehicleTypeResponse::from)
                        .toList(),
                priceRepository.findAllByEnabledTrueAndAirport_EnabledTrueAndVehicleType_EnabledTrue()
                        .stream()
                        .map(TransferPriceResponse::from)
                        .toList()
        );
    }

    @Transactional
    public TransferBookingResponse create(CreateTransferBookingRequest request) {
        return create(customerAccountService.currentCustomer(), request);
    }

    @Transactional
    public TransferBookingResponse create(
            CurrentCustomer customer,
            CreateTransferBookingRequest request
    ) {
        requireCustomerFlow(customer);
        bookingPolicy.requireBookable(request.pickupDate(), request.pickupTime());
        TransferAirport airport = airportRepository.findByIdAndEnabledTrue(request.airportId())
                .orElseThrow(() -> new TransferConfigurationUnavailableException("airport"));
        TransferVehicleType vehicle = vehicleRepository.findByIdAndEnabledTrue(request.vehicleTypeId())
                .orElseThrow(() -> new TransferConfigurationUnavailableException("vehicle type"));
        TransferPrice price = priceRepository
                .findByAirport_IdAndVehicleType_IdAndDirectionAndEnabledTrue(
                        airport.getId(),
                        vehicle.getId(),
                        request.direction()
                )
                .filter(candidate -> candidate.getAirport().isEnabled())
                .filter(candidate -> candidate.getVehicleType().isEnabled())
                .orElseThrow(() -> new TransferConfigurationUnavailableException("price"));

        String normalizedPhone = phoneNumberNormalizer.normalize(request.phone());
        customerAccountService.updateNormalizedPhone(customer.customerId(), normalizedPhone);
        var createdAt = clock.instant();
        TransferBooking booking = bookingRepository.saveAndFlush(new TransferBooking(
                new NewTransferBooking(
                        customer.customerId(),
                        customer.externalIdentityId(),
                        customer.displayName(),
                        normalizedPhone,
                        request.direction(),
                        airport,
                        vehicle,
                        request.pickupDate(),
                        request.pickupTime(),
                        request.address(),
                        request.passengerCount(),
                        request.luggageCount(),
                        request.flightNumber(),
                        request.scheduledArrivalTime(),
                        request.comment(),
                        price,
                        createdAt
                )
        ));
        customerAttributionService.attachOrganicFallback(
                customer.customerId(),
                createdAt,
                PlatformService.TRANSFER
        );
        TransferBookingResponse response = TransferBookingResponse.from(booking);
        eventPublisher.publishEvent(new TransferBookingCreatedEvent(
                response,
                booking.getCustomerId(),
                booking.getCommunicationIdentityId()
        ));
        return response;
    }

    @Transactional(readOnly = true)
    public List<TransferBookingResponse> currentCustomerBookings() {
        return currentCustomerBookings(customerAccountService.currentCustomer());
    }

    @Transactional(readOnly = true)
    public List<TransferBookingResponse> currentCustomerBookings(CurrentCustomer customer) {
        return bookingRepository.findAllByCustomerIdOrderByCreatedAtDesc(customer.customerId())
                .stream()
                .map(TransferBookingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransferBookingResponse currentCustomerBooking(long bookingId) {
        return currentCustomerBooking(customerAccountService.currentCustomer(), bookingId);
    }

    @Transactional(readOnly = true)
    public TransferBookingResponse currentCustomerBooking(
            CurrentCustomer customer,
            long bookingId
    ) {
        return TransferBookingResponse.from(requireCustomerBooking(bookingId, customer.customerId()));
    }

    @Transactional
    public TransferBookingResponse cancelCurrentCustomerBooking(long bookingId) {
        return cancel(customerAccountService.currentCustomer(), bookingId);
    }

    @Transactional
    public TransferBookingResponse cancel(CurrentCustomer customer, long bookingId) {
        TransferBooking booking = bookingRepository.findByIdForUpdate(bookingId)
                .filter(candidate -> candidate.getCustomerId() == customer.customerId())
                .orElseThrow(() -> new TransferBookingNotFoundException(bookingId));
        booking.cancelByCustomer(bookingPolicy.hasStarted(booking), clock.instant());
        publishStatus(booking, TransferBookingCustomerEvent.Type.CANCELLED);
        return TransferBookingResponse.from(booking);
    }

    private void publishStatus(TransferBooking booking, TransferBookingCustomerEvent.Type type) {
        eventPublisher.publishEvent(new TransferBookingCustomerEvent(
                booking.getId(), booking.getCustomerId(), booking.getCommunicationIdentityId(), type
        ));
    }

    private TransferBooking requireCustomerBooking(long bookingId, long customerId) {
        return bookingRepository.findByIdAndCustomerId(bookingId, customerId)
                .orElseThrow(() -> new TransferBookingNotFoundException(bookingId));
    }

    private void requireCustomerFlow(CurrentCustomer customer) {
        platformServiceAccessService.requireCanStartCustomerFlow(
                PlatformService.TRANSFER,
                customer.customerId()
        );
    }
}
