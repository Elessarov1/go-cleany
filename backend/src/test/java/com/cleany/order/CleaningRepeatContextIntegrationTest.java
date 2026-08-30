package com.cleany.order;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.catalog.PlatformServiceNotAvailableException;
import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.repeat.RepeatSourceNotEligibleException;

class CleaningRepeatContextIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CleaningOrderService orderService;

    @Autowired
    private CleaningOrderRepository orderRepository;

    @Autowired
    private CustomerAccountService customerAccountService;

    @Autowired
    private CustomerExternalIdentityRepository identityRepository;

    @Autowired
    private CustomerAccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Clock clock;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        orderRepository.deleteAll();
        jdbcTemplate.update("delete from customer_acquisition");
        identityRepository.deleteAll();
        accountRepository.deleteAll();
        jdbcTemplate.update("""
                update platform_service_state
                   set status = 'ENABLED',
                       updated_by_customer_id = null,
                       version = version + 1
                 where service = 'CLEANING'
                """);
        clearPlatformServiceStateCache();
    }

    @Test
    void completedOwnedOrder_prefillsReusableFieldsAndCreatesFreshLinkedOrder() {
        CurrentCustomer owner = customer("cleaning-repeat-owner");
        CleaningOrder source = orderService.createOrder(owner, command(
                ServiceArea.KESTEL,
                "Old address",
                ApartmentType.THREE_PLUS_ONE,
                true,
                CleaningType.DEEP,
                "+905551110000",
                "Old comment"
        ));
        jdbcTemplate.update(
                "update cleaning_order set status = 'COMPLETED', completed_at = ? where id = ?",
                Timestamp.from(clock.instant()),
                source.getId()
        );

        orderService.recordRepeatShown(owner, source.getId());
        orderService.recordRepeatShown(owner, source.getId());
        CleaningRepeatPrefillResponse prefill = orderService.repeatPrefill(owner, source.getId());
        orderService.repeatPrefill(owner, source.getId());
        CleaningOrder repeated = orderService.createOrder(owner, command(
                prefill.area(),
                prefill.address(),
                prefill.apartmentType(),
                prefill.duplex(),
                prefill.cleaningType(),
                "+905559990000",
                null
        ), source.getId());

        Assertions.assertAll(
                () -> Assertions.assertEquals(source.getId().longValue(), prefill.sourceOrderId()),
                () -> Assertions.assertEquals(ServiceArea.KESTEL, prefill.area()),
                () -> Assertions.assertEquals("Old address", prefill.address()),
                () -> Assertions.assertEquals(ApartmentType.THREE_PLUS_ONE, prefill.apartmentType()),
                () -> Assertions.assertTrue(prefill.duplex()),
                () -> Assertions.assertEquals(CleaningType.DEEP, prefill.cleaningType()),
                () -> Assertions.assertEquals(source.getId(), repeated.getRepeatSourceOrderId()),
                () -> Assertions.assertEquals("+905559990000", repeated.getPhone()),
                () -> Assertions.assertNull(repeated.getCustomerComment()),
                () -> Assertions.assertNotEquals(source.getRequestedDate(), repeated.getRequestedDate()),
                () -> Assertions.assertEquals(source.getPrice(), repeated.getPrice()),
                () -> Assertions.assertEquals(2L, jdbcTemplate.queryForObject(
                        "select count(*) from repeat_action_event where customer_id = ? and service = 'CLEANING'",
                        Long.class,
                        owner.customerId()
                ))
        );
    }

    @Test
    void repeatRejectsForeignNonCompletedAndDisabledSources() {
        CurrentCustomer owner = customer("cleaning-repeat-validation-owner");
        CurrentCustomer stranger = customer("cleaning-repeat-validation-stranger");
        CleaningOrder source = orderService.createOrder(owner, command(
                ServiceArea.MAHMUTLAR,
                "Validation address",
                ApartmentType.TWO_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                "+905551112233",
                null
        ));

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        RepeatSourceNotEligibleException.class,
                        () -> orderService.repeatPrefill(owner, source.getId())
                ),
                () -> Assertions.assertThrows(
                        OrderNotFoundException.class,
                        () -> orderService.repeatPrefill(stranger, source.getId())
                )
        );

        jdbcTemplate.update(
                "update cleaning_order set status = 'COMPLETED', completed_at = ? where id = ?",
                Timestamp.from(clock.instant()),
                source.getId()
        );
        jdbcTemplate.update("""
                update platform_service_state
                   set status = 'DISABLED', version = version + 1
                 where service = 'CLEANING'
                """);
        clearPlatformServiceStateCache();

        Assertions.assertThrows(
                PlatformServiceNotAvailableException.class,
                () -> orderService.repeatPrefill(owner, source.getId())
        );
    }

    private CreateCleaningOrderCommand command(
            ServiceArea area,
            String address,
            ApartmentType apartmentType,
            boolean duplex,
            CleaningType cleaningType,
            String phone,
            String comment
    ) {
        LocalDate requestedDate = LocalDate.now(clock.withZone(ZoneId.of("Europe/Istanbul")))
                .plusDays(comment == null ? 2 : 1);
        return new CreateCleaningOrderCommand(
                area,
                address,
                apartmentType,
                duplex,
                cleaningType,
                requestedDate,
                phone,
                comment,
                null,
                null
        );
    }

    private CurrentCustomer customer(String subject) {
        return customerAccountService.resolveCustomer(new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.TELEGRAM,
                subject,
                subject,
                "Customer " + subject,
                "ru"
        ));
    }
}
