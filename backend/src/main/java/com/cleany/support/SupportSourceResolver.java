package com.cleany.support;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.catalog.PlatformService;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderStatus;
import com.cleany.rental.RentalBookingRepository;
import com.cleany.rental.RentalBookingStatus;
import com.cleany.transfer.TransferBookingRepository;
import com.cleany.transfer.TransferBookingStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupportSourceResolver {

    private final CleaningOrderRepository cleaningRepository;
    private final RentalBookingRepository rentalRepository;
    private final TransferBookingRepository transferRepository;

    @Transactional(readOnly = true)
    public SupportSource requireOwned(
            PlatformService service,
            long sourceEntityId,
            long customerId
    ) {
        if (sourceEntityId <= 0 || customerId <= 0) {
            throw new SupportSourceNotFoundException();
        }
        return switch (service) {
            case CLEANING -> cleaningRepository.findByIdAndCustomerId(sourceEntityId, customerId)
                    .map(order -> new SupportSource(
                            service,
                            order.getId(),
                            order.getCustomerId(),
                            order.getStatus() == CleaningOrderStatus.COMPLETED,
                            order.getCustomerName(),
                            order.getPhone(),
                            "/cleaning/orders/" + order.getId(),
                            "/admin/cleaning/orders/" + order.getId()
                    ))
                    .orElseThrow(SupportSourceNotFoundException::new);
            case RENTAL -> rentalRepository.findByIdAndCustomerId(sourceEntityId, customerId)
                    .map(booking -> new SupportSource(
                            service,
                            booking.getId(),
                            booking.getCustomerId(),
                            booking.getStatus() == RentalBookingStatus.COMPLETED,
                            booking.getCustomerName(),
                            booking.getPhone(),
                            "/rent/bookings/" + booking.getId(),
                            "/admin/rent/bookings/" + booking.getId()
                    ))
                    .orElseThrow(SupportSourceNotFoundException::new);
            case TRANSFER -> transferRepository.findByIdAndCustomerId(sourceEntityId, customerId)
                    .map(booking -> new SupportSource(
                            service,
                            booking.getId(),
                            booking.getCustomerId(),
                            booking.getStatus() == TransferBookingStatus.COMPLETED,
                            booking.getCustomerNameSnapshot(),
                            booking.getCustomerPhoneSnapshot(),
                            "/transfer/bookings/" + booking.getId(),
                            "/admin/transfer/bookings/" + booking.getId()
                    ))
                    .orElseThrow(SupportSourceNotFoundException::new);
        };
    }
}
