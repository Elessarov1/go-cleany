package com.cleany.transfer;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferDriverAssignmentService {

    private final TransferProperties properties;
    private final TransferDriverRepository driverRepository;
    private final TransferBookingRepository bookingRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Transactional
    public TransferBookingResponse assignByAdmin(long bookingId, long driverId) {
        TransferDriver driver = driverRepository.findByIdAndEnabledTrue(driverId)
                .orElseThrow(() -> new TransferConfigurationUnavailableException("driver"));
        return assign(bookingId, driver);
    }

    @Transactional
    public TransferBookingResponse selfAccept(long bookingId, long telegramUserId) {
        if (properties.assignmentMode() != TransferAssignmentMode.DRIVER_SELF_ACCEPT) {
            throw new TransferAssignmentConflictException(bookingId);
        }
        TransferDriver driver = driverRepository
                .findByVerifiedTelegramUserIdAndEnabledTrue(telegramUserId)
                .filter(TransferDriver::canReceiveTelegramBookings)
                .orElseThrow(() -> new TransferDriverLinkException(
                        "Telegram is not connected to an enabled transfer driver"
                ));
        return assign(bookingId, driver);
    }

    private TransferBookingResponse assign(long bookingId, TransferDriver driver) {
        if (bookingRepository.assignRequestedBooking(bookingId, driver.getId(), clock.instant()) != 1) {
            throw new TransferAssignmentConflictException(bookingId);
        }
        TransferBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new TransferBookingNotFoundException(bookingId));
        eventPublisher.publishEvent(new TransferBookingCustomerEvent(
                booking.getId(), booking.getCustomerId(), booking.getCommunicationIdentityId()
        ));
        return TransferBookingResponse.from(booking);
    }
}
