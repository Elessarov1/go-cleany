package com.cleany.customer.activity;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.catalog.PlatformService;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderEvent;
import com.cleany.order.CleaningOrderEventRepository;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderStatus;
import com.cleany.order.ServiceArea;
import com.cleany.rental.RentalBooking;
import com.cleany.rental.RentalBookingRepository;
import com.cleany.rental.RentalBookingStatus;
import com.cleany.transfer.TransferBooking;
import com.cleany.transfer.TransferBookingRepository;
import com.cleany.transfer.TransferBookingStatus;
import com.cleany.transfer.TransferDirection;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerActivityService {

    private static final Comparator<CustomerActivityItem> UPCOMING_ORDER = Comparator
            .comparing(CustomerActivityItem::scheduledDate)
            .thenComparing(item -> item.scheduledTime() == null ? LocalTime.MIN : item.scheduledTime())
            .thenComparing(CustomerActivityItem::service)
            .thenComparingLong(CustomerActivityItem::entityId);

    private static final Comparator<CustomerActivityItem> HISTORY_ORDER = Comparator
            .comparing(CustomerActivityItem::occurredAt)
            .reversed()
            .thenComparing(CustomerActivityItem::service)
            .thenComparingLong(CustomerActivityItem::entityId);

    private final CustomerAccountService customerAccountService;
    private final CleaningOrderRepository cleaningOrderRepository;
    private final CleaningOrderEventRepository cleaningOrderEventRepository;
    private final RentalBookingRepository rentalBookingRepository;
    private final TransferBookingRepository transferBookingRepository;

    @Transactional(readOnly = true)
    public CustomerActivityResponse current() {
        return activity(customerAccountService.currentCustomer());
    }

    @Transactional(readOnly = true)
    public CustomerActivityResponse activity(CurrentCustomer customer) {
        long customerId = customer.customerId();
        List<CleaningOrder> cleaningOrders = cleaningOrderRepository
                .findAllByCustomerIdOrderByCreatedAtDesc(customerId);
        Map<Long, Instant> cleaningStatusTimes = cleaningStatusTimes(cleaningOrders);

        List<CustomerActivityItem> activeAndUpcoming = new ArrayList<>();
        List<CustomerActivityItem> history = new ArrayList<>();

        cleaningOrders.forEach(order -> add(
                cleaningItem(order, cleaningStatusTimes.get(order.getId())),
                isActive(order.getStatus()),
                activeAndUpcoming,
                history
        ));
        rentalBookingRepository.findAllByCustomerIdOrderByCreatedAtDesc(customerId)
                .forEach(booking -> add(
                        rentalItem(booking),
                        booking.getStatus() == RentalBookingStatus.CONFIRMED,
                        activeAndUpcoming,
                        history
                ));
        transferBookingRepository.findAllByCustomerIdOrderByCreatedAtDesc(customerId)
                .forEach(booking -> add(
                        transferItem(booking),
                        booking.getStatus() == TransferBookingStatus.REQUESTED
                                || booking.getStatus() == TransferBookingStatus.CONFIRMED,
                        activeAndUpcoming,
                        history
                ));

        activeAndUpcoming.sort(UPCOMING_ORDER);
        history.sort(HISTORY_ORDER);
        return new CustomerActivityResponse(List.copyOf(activeAndUpcoming), List.copyOf(history));
    }

    private Map<Long, Instant> cleaningStatusTimes(List<CleaningOrder> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> orderIds = orders.stream().map(CleaningOrder::getId).toList();
        Map<Long, Instant> result = new HashMap<>();
        for (CleaningOrderEvent event : cleaningOrderEventRepository
                .findAllByOrder_IdInOrderByOccurredAtAscIdAsc(orderIds)) {
            if (event.getToStatus() == event.getOrder().getStatus()) {
                result.put(event.getOrder().getId(), event.getOccurredAt());
            }
        }
        return result;
    }

    private static void add(
            CustomerActivityItem item,
            boolean active,
            List<CustomerActivityItem> activeAndUpcoming,
            List<CustomerActivityItem> history
    ) {
        (active ? activeAndUpcoming : history).add(item);
    }

    private static CustomerActivityItem cleaningItem(CleaningOrder order, Instant statusTime) {
        String areaRu = areaRu(order.getArea());
        String areaEn = areaEn(order.getArea());
        return new CustomerActivityItem(
                PlatformService.CLEANING,
                order.getId(),
                order.getStatus().name(),
                "Уборка квартиры",
                "Apartment cleaning",
                areaRu + " · " + order.getAddress(),
                areaEn + " · " + order.getAddress(),
                order.getRequestedDate(),
                null,
                null,
                cleaningOccurredAt(order, statusTime),
                order.getFinalCustomerPrice(),
                order.getCurrency(),
                "/cleaning/orders/" + order.getId()
        );
    }

    private static CustomerActivityItem rentalItem(RentalBooking booking) {
        return new CustomerActivityItem(
                PlatformService.RENTAL,
                booking.getId(),
                booking.getStatus().name(),
                fallback(booking.getProperty().getTitleRu(), "Аренда квартиры"),
                fallback(booking.getProperty().getTitleEn(), "Apartment rental"),
                booking.getProperty().getArea(),
                rentalAreaEn(booking.getProperty().getArea()),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                null,
                firstNonNull(booking.getCompletedAt(), booking.getCancelledAt(), booking.getCreatedAt()),
                booking.getTotalPrice(),
                booking.getCurrency(),
                "/rent/bookings/" + booking.getId()
        );
    }

    private static CustomerActivityItem transferItem(TransferBooking booking) {
        boolean toAirport = booking.getDirection() == TransferDirection.TO_AIRPORT;
        return new CustomerActivityItem(
                PlatformService.TRANSFER,
                booking.getId(),
                booking.getStatus().name(),
                toAirport
                        ? "Трансфер в аэропорт " + booking.getAirportCodeSnapshot()
                        : "Трансфер из аэропорта " + booking.getAirportCodeSnapshot(),
                toAirport
                        ? "Transfer to " + booking.getAirportCodeSnapshot() + " airport"
                        : "Transfer from " + booking.getAirportCodeSnapshot() + " airport",
                booking.getVehicleNameRuSnapshot(),
                booking.getVehicleNameEnSnapshot(),
                booking.getPickupDate(),
                null,
                booking.getPickupTime(),
                firstNonNull(
                        booking.getCompletedAt(),
                        booking.getCancelledAt(),
                        booking.getRejectedAt(),
                        booking.getConfirmedAt(),
                        booking.getCreatedAt()
                ),
                booking.getPriceAmount(),
                booking.getPriceCurrency(),
                "/transfer/bookings/" + booking.getId()
        );
    }

    private static boolean isActive(CleaningOrderStatus status) {
        return status == CleaningOrderStatus.NEW
                || status == CleaningOrderStatus.ACCEPTED
                || status == CleaningOrderStatus.AWAITING_REPORT
                || status == CleaningOrderStatus.ONSITE_ISSUE_REPORTED;
    }

    private static Instant cleaningOccurredAt(CleaningOrder order, Instant statusTime) {
        if (statusTime != null) {
            return statusTime;
        }
        if (order.getStatus() == CleaningOrderStatus.COMPLETED && order.getCompletedAt() != null) {
            return order.getCompletedAt();
        }
        if (isActive(order.getStatus()) && order.getAcceptedAt() != null) {
            return order.getAcceptedAt();
        }
        return order.getCreatedAt();
    }

    private static String areaRu(ServiceArea area) {
        return switch (area) {
            case MAHMUTLAR -> "Махмутлар";
            case KARGICAK -> "Каргыджак";
            case KESTEL -> "Кестель";
        };
    }

    private static String areaEn(ServiceArea area) {
        return switch (area) {
            case MAHMUTLAR -> "Mahmutlar";
            case KARGICAK -> "Kargıcak";
            case KESTEL -> "Kestel";
        };
    }

    private static String rentalAreaEn(String area) {
        if (area == null) {
            return null;
        }
        return switch (area) {
            case "Махмутлар" -> "Mahmutlar";
            case "Каргыджак" -> "Kargıcak";
            case "Кестель" -> "Kestel";
            default -> area;
        };
    }

    private static String fallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static Instant firstNonNull(Instant... values) {
        for (Instant value : values) {
            if (value != null) {
                return value;
            }
        }
        throw new IllegalStateException("Customer activity item has no occurrence timestamp");
    }
}
