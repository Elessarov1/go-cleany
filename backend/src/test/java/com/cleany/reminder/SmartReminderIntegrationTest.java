package com.cleany.reminder;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.catalog.PlatformService;
import com.cleany.catalog.PlatformServiceStatus;
import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerIdentityProvider;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.notification.CustomerNotificationType;
import com.cleany.order.ApartmentType;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderService;
import com.cleany.order.CleaningType;
import com.cleany.order.CreateCleaningOrderCommand;
import com.cleany.order.OrderNotFoundException;
import com.cleany.order.ServiceArea;

class SmartReminderIntegrationTest extends BaseIntegrationTest {

    private static final ZoneId ISTANBUL = ZoneId.of("Europe/Istanbul");

    @Autowired private CleaningRepeatReminderService cleaningReminderService;
    @Autowired private SmartReminderService smartReminderService;
    @Autowired private CustomerReminderRepository reminderRepository;
    @Autowired private CleaningOrderService cleaningOrderService;
    @Autowired private CustomerAccountService customerAccountService;
    @Autowired private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private CustomerIdentityProvider identityProvider;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from customer_notification");
        reminderRepository.deleteAll();
        jdbcTemplate.update("delete from cleaning_order_event");
        jdbcTemplate.update("delete from cleaning_order_photo");
        jdbcTemplate.update("delete from cleaning_order");
        jdbcTemplate.update("delete from customer_acquisition");
        jdbcTemplate.update("delete from customer_external_identity");
        jdbcTemplate.update("delete from customer_account");
        jdbcTemplate.update("update platform_service_state set status = 'ENABLED', version = version + 1");
        clearPlatformServiceStateCache();
        Mockito.reset(identityProvider);
    }

    @Test
    void completedOrder_ownerCanConfigureAndFinalReminderCannotBeChanged() {
        CurrentCustomer owner = customer("reminder-owner");
        CurrentCustomer outsider = customer("reminder-outsider");
        Instant completedAt = Instant.now().minusSeconds(20L * 86400);
        CleaningOrder order = completedCleaning(owner, "Atatürk Cd. 10", completedAt);

        authenticate(owner);
        CleaningRepeatReminderResponse empty = cleaningReminderService.current(order.getId());
        CleaningRepeatReminderResponse inTwoWeeks = cleaningReminderService.update(
                order.getId(),
                CleaningRepeatReminderSelection.IN_14_DAYS
        );
        CleaningRepeatReminderResponse inMonth = cleaningReminderService.update(
                order.getId(),
                CleaningRepeatReminderSelection.IN_30_DAYS
        );
        CleaningRepeatReminderResponse disabled = cleaningReminderService.update(
                order.getId(),
                CleaningRepeatReminderSelection.DO_NOT_REMIND
        );

        authenticate(outsider);
        Assertions.assertThrows(
                OrderNotFoundException.class,
                () -> cleaningReminderService.current(order.getId())
        );

        authenticate(owner);
        cleaningReminderService.update(order.getId(), CleaningRepeatReminderSelection.IN_14_DAYS);
        smartReminderService.process();

        Assertions.assertAll(
                () -> Assertions.assertNull(empty.status()),
                () -> Assertions.assertEquals(
                        completedAt.atZone(ISTANBUL).toLocalDate().plusDays(14),
                        inTwoWeeks.scheduledDate()
                ),
                () -> Assertions.assertEquals(
                        completedAt.atZone(ISTANBUL).toLocalDate().plusDays(30),
                        inMonth.scheduledDate()
                ),
                () -> Assertions.assertEquals(CustomerReminderStatus.DISABLED, disabled.status()),
                () -> Assertions.assertThrows(
                        ReminderFinalStateException.class,
                        () -> cleaningReminderService.update(
                                order.getId(),
                                CleaningRepeatReminderSelection.IN_30_DAYS
                        )
                )
        );
    }

    @Test
    void dueCleaningReminder_isDurableIdempotentAndRetriesServiceAvailability() {
        CurrentCustomer owner = customer("reminder-durable");
        Instant completedAt = Instant.now().minusSeconds(14L * 86400);
        CleaningOrder order = completedCleaning(owner, "Barbaros  25", completedAt);
        authenticate(owner);
        cleaningReminderService.update(order.getId(), CleaningRepeatReminderSelection.IN_14_DAYS);
        setServiceStatus(PlatformServiceStatus.DISABLED);

        SmartReminderProcessingResult unavailable = smartReminderService.process();
        setServiceStatus(PlatformServiceStatus.ENABLED);
        SmartReminderProcessingResult delivered = smartReminderService.process();
        SmartReminderProcessingResult duplicate = smartReminderService.process();

        CustomerReminder reminder = reminderRepository.findAll().getFirst();
        long notificationCount = jdbcTemplate.queryForObject(
                "select count(*) from customer_notification where customer_id = ? and type = ?",
                Long.class,
                owner.customerId(),
                CustomerNotificationType.CLEANING_REPEAT_REMINDER.name()
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(0, unavailable.notified()),
                () -> Assertions.assertEquals(1, delivered.notified()),
                () -> Assertions.assertEquals(0, duplicate.notified()),
                () -> Assertions.assertEquals(CustomerReminderStatus.NOTIFIED, reminder.getStatus()),
                () -> Assertions.assertNotNull(reminder.getNotifiedAt()),
                () -> Assertions.assertEquals(1, notificationCount)
        );
    }

    @Test
    void normalizedLaterOrderSupersedesWhileCancelledOrderDoesNot() {
        CurrentCustomer owner = customer("reminder-supersede");
        Instant completedAt = Instant.now().minusSeconds(14L * 86400);
        CleaningOrder source = completedCleaning(owner, "  BARBAROS   Cd. 25  ", completedAt);
        CleaningOrder cancelled = cleaning(owner, "barbaros cd. 25");
        jdbcTemplate.update("update cleaning_order set status = 'CANCELLED' where id = ?", cancelled.getId());
        authenticate(owner);

        CleaningRepeatReminderResponse pending = cleaningReminderService.update(
                source.getId(),
                CleaningRepeatReminderSelection.IN_14_DAYS
        );
        cleaning(owner, "Barbaros cd.  25");
        smartReminderService.process();
        CleaningRepeatReminderResponse superseded = cleaningReminderService.current(source.getId());

        Assertions.assertAll(
                () -> Assertions.assertEquals(CustomerReminderStatus.PENDING, pending.status()),
                () -> Assertions.assertEquals(CustomerReminderStatus.SUPERSEDED, superseded.status()),
                () -> Assertions.assertFalse(superseded.editable())
        );
    }

    private CurrentCustomer customer(String subject) {
        return customerAccountService.resolveCustomer(identity(subject));
    }

    private void authenticate(CurrentCustomer customer) {
        Mockito.when(identityProvider.currentIdentity()).thenReturn(identity(customer.externalSubject()));
    }

    private static AuthenticatedCustomerIdentity identity(String subject) {
        return new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.GOOGLE,
                subject,
                null,
                "Reminder customer",
                "en",
                subject + "@example.test",
                true,
                false
        );
    }

    private CleaningOrder completedCleaning(
            CurrentCustomer customer,
            String address,
            Instant completedAt
    ) {
        CleaningOrder order = cleaning(customer, address);
        jdbcTemplate.update(
                "update cleaning_order set status = 'COMPLETED', completed_at = ? where id = ?",
                Timestamp.from(completedAt),
                order.getId()
        );
        return order;
    }

    private CleaningOrder cleaning(CurrentCustomer customer, String address) {
        return cleaningOrderService.createOrder(customer, new CreateCleaningOrderCommand(
                ServiceArea.MAHMUTLAR,
                address,
                ApartmentType.ONE_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                LocalDate.now(ISTANBUL).plusDays(1),
                "+905551112233",
                null,
                null,
                null
        ));
    }

    private void setServiceStatus(PlatformServiceStatus status) {
        jdbcTemplate.update(
                "update platform_service_state set status = ?, version = version + 1 where service = ?",
                status.name(),
                PlatformService.CLEANING.name()
        );
        clearPlatformServiceStateCache();
    }
}
