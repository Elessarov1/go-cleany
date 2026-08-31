package com.cleany.customer.home;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.catalog.PlatformService;
import com.cleany.catalog.PlatformServiceAccessService;
import com.cleany.configuration.CleaningProperties;
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitStatus;
import com.cleany.crossservice.rentalcleaning.RentalCleaningContextResponse;
import com.cleany.crossservice.rentalcleaning.RentalCleaningContextService;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextAvailability;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextOptionResponse;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextService;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextType;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.activity.CustomerActivityItem;
import com.cleany.customer.activity.CustomerActivityResponse;
import com.cleany.customer.activity.CustomerActivityService;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderStatus;
import com.cleany.rental.RentalBooking;
import com.cleany.rental.RentalBookingRepository;
import com.cleany.transfer.TransferBooking;
import com.cleany.transfer.TransferBookingRepository;
import com.cleany.transfer.TransferBookingStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerHomeService {

    private static final Comparator<CustomerHomePrimaryAction> PRIMARY_ACTION_ORDER = Comparator
            .comparing(CustomerHomePrimaryAction::relevantDate)
            .thenComparingInt(action -> action.type().priority())
            .thenComparingLong(CustomerHomePrimaryAction::sourceEntityId);

    private static final Comparator<CustomerHomeRepeatOpportunity> REPEAT_ORDER = Comparator
            .comparing(CustomerHomeRepeatOpportunity::sourceCompletedAt)
            .reversed()
            .thenComparing(CustomerHomeRepeatOpportunity::service)
            .thenComparingLong(CustomerHomeRepeatOpportunity::sourceEntityId);

    private final CustomerAccountService customerAccountService;
    private final CustomerActivityService customerActivityService;
    private final RentalBookingRepository rentalBookingRepository;
    private final CleaningOrderRepository cleaningOrderRepository;
    private final TransferBookingRepository transferBookingRepository;
    private final RentalTransferContextService rentalTransferContextService;
    private final RentalCleaningContextService rentalCleaningContextService;
    private final PlatformServiceAccessService serviceAccessService;
    private final CleaningProperties cleaningProperties;
    private final Clock clock;

    @Transactional(readOnly = true)
    public CustomerHomeResponse current() {
        return home(customerAccountService.currentCustomer());
    }

    @Transactional(readOnly = true)
    public CustomerHomeResponse home(CurrentCustomer customer) {
        CustomerActivityResponse activity = customerActivityService.activity(customer);
        CustomerActivityItem activeTransaction = activity.activeAndUpcoming().stream()
                .findFirst()
                .orElse(null);
        CustomerHomePrimaryAction primaryAction = primaryAction(customer);
        CustomerHomeRepeatOpportunity repeatOpportunity = repeatOpportunity(
                customer,
                activity,
                primaryAction
        );
        return new CustomerHomeResponse(
                activeTransaction != null || !activity.history().isEmpty(),
                activeTransaction,
                activity.activeAndUpcoming().size(),
                primaryAction,
                repeatOpportunity
        );
    }

    private CustomerHomePrimaryAction primaryAction(CurrentCustomer customer) {
        LocalDate today = LocalDate.now(clock.withZone(cleaningProperties.zoneId()));
        List<CustomerHomePrimaryAction> candidates = new ArrayList<>();
        for (RentalBooking booking : rentalBookingRepository
                .findAllByCustomerIdOrderByCreatedAtDesc(customer.customerId())) {
            rentalTransferContextService.context(customer, booking.getId()).options().stream()
                    .filter(option -> option.availability()
                            == RentalTransferContextAvailability.BOOKABLE)
                    .map(option -> transferAction(booking.getId(), option))
                    .forEach(candidates::add);
            cleaningAction(customer, booking.getId(), today).ifPresent(candidates::add);
        }
        return candidates.stream().min(PRIMARY_ACTION_ORDER).orElse(null);
    }

    private Optional<CustomerHomePrimaryAction> cleaningAction(
            CurrentCustomer customer,
            long rentalBookingId,
            LocalDate today
    ) {
        RentalCleaningContextResponse context = rentalCleaningContextService.context(
                customer,
                rentalBookingId
        );
        if (!context.cleaningFlowAvailable()
                || context.benefitStatus() != RentalCleaningBenefitStatus.AVAILABLE
                || context.promoCode() == null
                || context.checkOutDate().isBefore(today)) {
            return Optional.empty();
        }
        LocalDate relevantDate = context.earliestBenefitCleaningDate().isAfter(today)
                ? context.earliestBenefitCleaningDate()
                : today;
        return Optional.of(new CustomerHomePrimaryAction(
                CustomerHomePrimaryActionType.RENTAL_CLEANING,
                PlatformService.RENTAL,
                rentalBookingId,
                PlatformService.CLEANING,
                relevantDate,
                context.earliestBenefitCleaningDate(),
                context.checkOutDate(),
                "/cleaning?rentalBooking=" + rentalBookingId + "&promo=" + context.promoCode()
        ));
    }

    private static CustomerHomePrimaryAction transferAction(
            long rentalBookingId,
            RentalTransferContextOptionResponse option
    ) {
        CustomerHomePrimaryActionType type = option.context()
                == RentalTransferContextType.ARRIVAL
                ? CustomerHomePrimaryActionType.RENTAL_TRANSFER_ARRIVAL
                : CustomerHomePrimaryActionType.RENTAL_TRANSFER_CHECKOUT;
        return new CustomerHomePrimaryAction(
                type,
                PlatformService.RENTAL,
                rentalBookingId,
                PlatformService.TRANSFER,
                option.suggestedDate(),
                null,
                null,
                "/transfer?rentalBooking=" + rentalBookingId
                        + "&rentalContext=" + option.context()
        );
    }

    private CustomerHomeRepeatOpportunity repeatOpportunity(
            CurrentCustomer customer,
            CustomerActivityResponse activity,
            CustomerHomePrimaryAction primaryAction
    ) {
        Set<PlatformService> activeServices = EnumSet.noneOf(PlatformService.class);
        activity.activeAndUpcoming().stream()
                .map(CustomerActivityItem::service)
                .forEach(activeServices::add);
        PlatformService primaryTarget = primaryAction == null ? null : primaryAction.targetService();
        List<CustomerHomeRepeatOpportunity> candidates = new ArrayList<>();
        if (!activeServices.contains(PlatformService.CLEANING)
                && primaryTarget != PlatformService.CLEANING
                && serviceAccessService.canStartCustomerFlow(
                        PlatformService.CLEANING,
                        customer.customerId()
                )) {
            cleaningOrderRepository.findAllByCustomerIdOrderByCreatedAtDesc(customer.customerId())
                    .stream()
                    .filter(order -> order.getStatus() == CleaningOrderStatus.COMPLETED)
                    .filter(order -> order.getCompletedAt() != null)
                    .map(CustomerHomeService::cleaningRepeat)
                    .forEach(candidates::add);
        }
        if (!activeServices.contains(PlatformService.TRANSFER)
                && primaryTarget != PlatformService.TRANSFER
                && serviceAccessService.canStartCustomerFlow(
                        PlatformService.TRANSFER,
                        customer.customerId()
                )) {
            transferBookingRepository.findAllByCustomerIdOrderByCreatedAtDesc(customer.customerId())
                    .stream()
                    .filter(booking -> booking.getStatus() == TransferBookingStatus.COMPLETED)
                    .filter(booking -> booking.getCompletedAt() != null)
                    .map(CustomerHomeService::transferRepeat)
                    .forEach(candidates::add);
        }
        return candidates.stream().min(REPEAT_ORDER).orElse(null);
    }

    private static CustomerHomeRepeatOpportunity cleaningRepeat(CleaningOrder order) {
        return new CustomerHomeRepeatOpportunity(
                PlatformService.CLEANING,
                order.getId(),
                order.getCompletedAt(),
                "/cleaning?repeatFrom=" + order.getId()
        );
    }

    private static CustomerHomeRepeatOpportunity transferRepeat(TransferBooking booking) {
        return new CustomerHomeRepeatOpportunity(
                PlatformService.TRANSFER,
                booking.getId(),
                booking.getCompletedAt(),
                "/transfer?repeatFrom=" + booking.getId()
        );
    }
}
