package com.cleany.transfer;

import java.time.Clock;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import com.cleany.analytics.CustomerAttributionService;
import com.cleany.catalog.PlatformService;
import com.cleany.analytics.RepeatActionEventType;
import com.cleany.analytics.RepeatActionTrackingService;
import com.cleany.catalog.PlatformServiceAccessService;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.repeat.RepeatSourceNotEligibleException;
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
    private final RepeatActionTrackingService repeatActionTrackingService;
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
        TransferBooking repeatSource = request.repeatFromBookingId() == null
                ? null
                : requireRepeatSource(customer, request.repeatFromBookingId());
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
        TransferBooking booking = new TransferBooking(
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
        );
        if (repeatSource != null) {
            booking.markRepeatOf(repeatSource.getId());
        }
        booking = bookingRepository.saveAndFlush(booking);
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

    @Transactional
    public void recordRepeatShown(long bookingId) {
        recordRepeatShown(customerAccountService.currentCustomer(), bookingId);
    }

    @Transactional
    public void recordRepeatShown(CurrentCustomer customer, long bookingId) {
        TransferBooking source = requireRepeatSource(customer, bookingId);
        repeatActionTrackingService.record(
                customer.customerId(),
                PlatformService.TRANSFER,
                source.getId(),
                RepeatActionEventType.CTA_SHOWN
        );
    }

    @Transactional
    public TransferRepeatPrefillResponse repeatPrefill(long bookingId) {
        return repeatPrefill(customerAccountService.currentCustomer(), bookingId);
    }

    @Transactional
    public TransferRepeatPrefillResponse repeatPrefill(CurrentCustomer customer, long bookingId) {
        TransferBooking source = requireRepeatSource(customer, bookingId);
        boolean currentPairAvailable = source.getAirport().isEnabled()
                && source.getVehicleType().isEnabled()
                && priceRepository.findByAirport_IdAndVehicleType_IdAndDirectionAndEnabledTrue(
                        source.getAirport().getId(),
                        source.getVehicleType().getId(),
                        source.getDirection()
                ).isPresent();
        repeatActionTrackingService.record(
                customer.customerId(),
                PlatformService.TRANSFER,
                source.getId(),
                RepeatActionEventType.PREFILL_STARTED
        );
        return new TransferRepeatPrefillResponse(
                source.getId(),
                source.getDirection(),
                currentPairAvailable ? source.getAirport().getId() : null,
                currentPairAvailable ? source.getVehicleType().getId() : null,
                source.getAddress(),
                currentPairAvailable
                        ? Math.min(source.getPassengerCount(), source.getVehicleType().getMaxPassengers())
                        : source.getPassengerCount(),
                currentPairAvailable
                        ? Math.min(source.getLuggageCount(), source.getVehicleType().getMaxLuggage())
                        : source.getLuggageCount()
        );
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
        publishStatus(booking);
        return TransferBookingResponse.from(booking);
    }

    private void publishStatus(TransferBooking booking) {
        eventPublisher.publishEvent(new TransferBookingCustomerEvent(
                booking.getId(), booking.getCustomerId(), booking.getCommunicationIdentityId()
        ));
    }

    private TransferBooking requireCustomerBooking(long bookingId, long customerId) {
        return bookingRepository.findByIdAndCustomerId(bookingId, customerId)
                .orElseThrow(() -> new TransferBookingNotFoundException(bookingId));
    }

    private TransferBooking requireRepeatSource(CurrentCustomer customer, long bookingId) {
        requireCustomerFlow(customer);
        TransferBooking source = requireCustomerBooking(bookingId, customer.customerId());
        if (source.getStatus() != TransferBookingStatus.COMPLETED) {
            throw new RepeatSourceNotEligibleException("Transfer booking", bookingId);
        }
        return source;
    }

    private void requireCustomerFlow(CurrentCustomer customer) {
        platformServiceAccessService.requireCanStartCustomerFlow(
                PlatformService.TRANSFER,
                customer.customerId()
        );
    }
}
