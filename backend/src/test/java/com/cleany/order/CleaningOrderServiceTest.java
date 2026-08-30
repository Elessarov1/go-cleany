package com.cleany.order;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import com.cleany.catalog.PlatformServiceAccessService;
import com.cleany.analytics.CustomerAttributionService;
import com.cleany.analytics.RepeatActionTrackingService;
import com.cleany.catalog.PlatformService;
import com.cleany.catalog.PlatformServiceNotAvailableException;
import com.cleany.configuration.CleanerProperties;
import com.cleany.configuration.CleaningProperties;
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitService;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.finance.AcquisitionSource;
import com.cleany.finance.CustomerDiscountType;
import com.cleany.finance.OrderFinancialSnapshot;
import com.cleany.media.MediaProvider;
import com.cleany.media.MediaProviderReferenceData;
import com.cleany.media.MediaProviderReferenceService;
import com.cleany.media.MediaUpload;
import com.cleany.media.StoredMedia;
import com.cleany.media.StoredProviderMedia;
import com.cleany.pricing.CleaningPriceService;
import com.cleany.referral.OrderReferralPlan;
import com.cleany.referral.ReferralUnlockedEvent;
import com.cleany.referral.ReferralService;

class CleaningOrderServiceTest {

    private static final Instant NOW = Instant.parse("2026-08-17T09:00:00Z");
    private static final long CLEANER_ID = 123456789L;
    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xD9};

    private CleaningOrderRepository repository;
    private CleaningOrderPhotoRepository photoRepository;
    private CleaningOrderEventRepository eventRepository;
    private PlatformServiceAccessService platformServiceAccessService;
    private ApplicationEventPublisher eventPublisher;
    private CustomerAccountService customerAccountService;
    private ReferralService referralService;
    private RentalCleaningBenefitService rentalCleaningBenefitService;
    private MediaProviderReferenceService mediaProviderReferenceService;
    private CustomerAttributionService customerAttributionService;
    private CleaningOrderService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(CleaningOrderRepository.class);
        photoRepository = Mockito.mock(CleaningOrderPhotoRepository.class);
        eventRepository = Mockito.mock(CleaningOrderEventRepository.class);
        platformServiceAccessService = Mockito.mock(PlatformServiceAccessService.class);
        eventPublisher = Mockito.mock(ApplicationEventPublisher.class);
        customerAccountService = Mockito.mock(CustomerAccountService.class);
        referralService = Mockito.mock(ReferralService.class);
        rentalCleaningBenefitService = Mockito.mock(RentalCleaningBenefitService.class);
        mediaProviderReferenceService = Mockito.mock(MediaProviderReferenceService.class);
        customerAttributionService = Mockito.mock(CustomerAttributionService.class);
        CleaningProperties properties = properties();
        Mockito.when(customerAccountService.currentCustomer()).thenReturn(
                new CurrentCustomer(
                        77L,
                        88L,
                        ExternalIdentityProvider.GOOGLE,
                        "905551234567",
                        null,
                        "Alex",
                        "ru"
                )
        );
        Mockito.when(referralService.planForCreation(
                Mockito.anyLong(),
                Mockito.nullable(String.class),
                Mockito.any(BigDecimal.class),
                Mockito.anyBoolean()
        )).thenAnswer(invocation -> new OrderReferralPlan(
                organicSnapshot(invocation.getArgument(2)),
                null,
                null,
                null,
                null
        ));
        service = new CleaningOrderService(
                repository,
                photoRepository,
                eventRepository,
                platformServiceAccessService,
                new CleaningPriceService(properties),
                new PhoneNumberNormalizer(),
                properties,
                new CleanerProperties(List.of(CLEANER_ID)),
                customerAccountService,
                referralService,
                rentalCleaningBenefitService,
                mediaProviderReferenceService,
                customerAttributionService,
                Mockito.mock(RepeatActionTrackingService.class),
                Clock.fixed(NOW, ZoneOffset.UTC),
                eventPublisher
        );
    }

    @Test
    void explicitCustomerOrder_usesSuppliedCustomerWithoutResolvingRequestIdentity() {
        Mockito.when(repository.save(Mockito.any(CleaningOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        var customer = new CurrentCustomer(
                91L,
                92L,
                ExternalIdentityProvider.GOOGLE,
                "905559876543",
                null,
                "Web Alex",
                "ru"
        );
        var command = new CreateCleaningOrderCommand(
                ServiceArea.MAHMUTLAR,
                " Barbaros Cd. 24 ",
                ApartmentType.TWO_PLUS_ONE,
                true,
                CleaningType.REGULAR,
                LocalDate.of(2026, 8, 18),
                " +90 555 123 45 67 ",
                " Key with security ",
                null
        );

        service.createOrder(customer, command);

        var captor = ArgumentCaptor.forClass(CleaningOrder.class);
        Mockito.verify(repository).save(captor.capture());
        CleaningOrder order = captor.getValue();
        Assertions.assertEquals(91L, order.getCustomerId());
        Assertions.assertEquals(92L, order.getCommunicationIdentityId());
        Assertions.assertEquals(0, order.getPrice().compareTo(BigDecimal.valueOf(1400)));
        Assertions.assertEquals(0, order.getBaseCommission().compareTo(new BigDecimal("210.00")));
        Assertions.assertEquals("TRY", order.getCurrency());
        Assertions.assertEquals(CleaningOrderStatus.NEW, order.getStatus());
        Assertions.assertEquals("Barbaros Cd. 24", order.getAddress());
        Assertions.assertEquals("+905551234567", order.getPhone());
        var eventCaptor = ArgumentCaptor.forClass(CleaningOrderEvent.class);
        Mockito.verify(eventRepository).save(eventCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(OrderEventType.CREATED, eventCaptor.getValue().getEventType()),
                () -> Assertions.assertEquals(OrderActorType.CUSTOMER, eventCaptor.getValue().getActorType()),
                () -> Assertions.assertNull(eventCaptor.getValue().getActorTelegramUserId()),
                () -> Assertions.assertEquals(CleaningOrderStatus.NEW, eventCaptor.getValue().getToStatus())
        );
        Mockito.verify(eventPublisher).publishEvent(Mockito.any(CleaningOrderCreatedEvent.class));
        Mockito.verify(customerAccountService, Mockito.never()).currentCustomer();
    }

    @Test
    void explicitCustomerQuote_usesSuppliedCustomerWithoutResolvingRequestIdentity() {
        var customer = new CurrentCustomer(
                91L,
                92L,
                ExternalIdentityProvider.GOOGLE,
                "905559876543",
                null,
                "Web Alex",
                "ru"
        );
        var request = new CleaningOrderQuoteRequest(
                ApartmentType.TWO_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                "PARTNER10"
        );
        Mockito.when(referralService.quote(
                Mockito.eq(91L),
                Mockito.eq("PARTNER10"),
                Mockito.any(BigDecimal.class),
                Mockito.eq(true)
        )).thenAnswer(invocation -> new OrderReferralPlan(
                organicSnapshot(invocation.getArgument(2)),
                null,
                null,
                null,
                null
        ));

        CleaningOrderQuoteResponse response = service.quoteOrder(customer, request);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, new BigDecimal("1100.00").compareTo(response.basePrice())),
                () -> Assertions.assertEquals("TRY", response.currency())
        );
        Mockito.verify(referralService).quote(
                Mockito.eq(91L),
                Mockito.eq("PARTNER10"),
                Mockito.any(BigDecimal.class),
                Mockito.eq(true)
        );
        Mockito.verify(customerAccountService, Mockito.never()).currentCustomer();
    }

    @Test
    void unavailableCleaningCannotBeBypassedWithReferralCode() {
        CurrentCustomer customer = new CurrentCustomer(
                91L,
                92L,
                ExternalIdentityProvider.GOOGLE,
                "905559876543",
                null,
                "Web Alex",
                "ru"
        );
        var command = new CreateCleaningOrderCommand(
                ServiceArea.MAHMUTLAR,
                "Barbaros Cd. 24",
                ApartmentType.TWO_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                LocalDate.of(2026, 8, 18),
                "+90 555 123 45 67",
                null,
                "FRIEND10"
        );
        Mockito.doThrow(new PlatformServiceNotAvailableException(PlatformService.CLEANING))
                .when(platformServiceAccessService)
                .requireCanStartCustomerFlow(PlatformService.CLEANING, customer.customerId());

        Assertions.assertThrows(
                PlatformServiceNotAvailableException.class,
                () -> service.createOrder(customer, command)
        );

        Mockito.verifyNoInteractions(referralService, repository, eventRepository, eventPublisher);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "+90 555 123 45 67|+905551234567",
            "+905551234567|+905551234567",
            "+7 999 123-45-67|+79991234567",
            "+90 (555) 123-45-67|+905551234567"
    })
    void orderRequest_validInternationalPhone_normalizedBeforeStorage(String rawPhone, String expectedPhone) {
        Mockito.when(repository.save(Mockito.any(CleaningOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.createOrder(validCommand(rawPhone));

        var captor = ArgumentCaptor.forClass(CleaningOrder.class);
        Mockito.verify(repository).save(captor.capture());
        Assertions.assertEquals(expectedPhone, captor.getValue().getPhone());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "05551234567",
            "9991234567",
            "+90",
            "12345",
            "abc"
    })
    void orderRequest_invalidPhone_orderNotCreated(String rawPhone) {
        Assertions.assertThrows(
                InvalidPhoneNumberException.class,
                () -> service.createOrder(validCommand(rawPhone))
        );

        Mockito.verifyNoInteractions(repository, eventRepository, eventPublisher);
    }

    @Test
    void orderRequest_dateOutsideBookingHorizon_exceptionThrown() {
        var command = new CreateCleaningOrderCommand(
                ServiceArea.KESTEL,
                "Address",
                ApartmentType.STUDIO,
                false,
                CleaningType.REGULAR,
                LocalDate.of(2026, 8, 25),
                "+90 555",
                null,
                null
        );

        Assertions.assertThrows(
                BookingDateNotAvailableException.class,
                () -> service.createOrder(command)
        );
    }

    @Test
    void newOrder_configuredCleanerClaims_orderAcceptedByConditionalUpdate() {
        CleaningOrder acceptedOrder = Mockito.mock(CleaningOrder.class);
        Mockito.when(acceptedOrder.getId()).thenReturn(43L);
        Mockito.when(acceptedOrder.getCustomerId()).thenReturn(77L);
        Mockito.when(acceptedOrder.getCommunicationIdentityId()).thenReturn(88L);
        Mockito.when(repository.claimNewOrder(
                43L,
                CLEANER_ID,
                NOW,
                CleaningOrderStatus.NEW,
                CleaningOrderStatus.ACCEPTED
        )).thenReturn(1);
        Mockito.when(repository.findById(43L)).thenReturn(Optional.of(acceptedOrder));

        CleaningOrder result = service.acceptOrder(43L, CLEANER_ID);

        Assertions.assertSame(acceptedOrder, result);
        Mockito.verify(repository).claimNewOrder(
                43L,
                CLEANER_ID,
                NOW,
                CleaningOrderStatus.NEW,
                CleaningOrderStatus.ACCEPTED
        );
        Mockito.verify(eventPublisher).publishEvent(
                new CleaningOrderCustomerEvent.Accepted(43L, 77L, 88L)
        );
    }

    @Test
    void alreadyClaimedOrder_secondCleanerAttemptsClaim_conflictThrown() {
        Mockito.when(repository.claimNewOrder(
                43L,
                CLEANER_ID,
                NOW,
                CleaningOrderStatus.NEW,
                CleaningOrderStatus.ACCEPTED
        )).thenReturn(0);

        Assertions.assertThrows(
                OrderClaimConflictException.class,
                () -> service.acceptOrder(43L, CLEANER_ID)
        );
    }

    @Test
    void acceptedOrder_assignedCleanerCancels_orderCancelled() {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(order.getId()).thenReturn(43L);
        Mockito.when(order.getCustomerId()).thenReturn(77L);
        Mockito.when(order.getCommunicationIdentityId()).thenReturn(88L);
        Mockito.when(order.getStatus()).thenReturn(CleaningOrderStatus.ACCEPTED);
        Mockito.when(repository.findById(43L)).thenReturn(Optional.of(order));

        CleaningOrder result = service.cancelOrderByCleaner(43L, CLEANER_ID);

        Assertions.assertSame(order, result);
        Mockito.verify(order).cancelByCleaner(CLEANER_ID);
        Mockito.verify(eventPublisher).publishEvent(
                new CleaningOrderCustomerEvent.Cancelled(43L, 77L, 88L)
        );
        Mockito.verify(eventPublisher, Mockito.never())
                .publishEvent(Mockito.isA(ReferralUnlockedEvent.class));
    }

    @Test
    void acceptedOrder_unconfiguredCleanerAttemptsCancel_authorizationRejected() {
        Assertions.assertThrows(
                CleanerNotAuthorizedException.class,
                () -> service.cancelOrderByCleaner(43L, 777L)
        );
        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void acceptedOrder_finishSelected_reportCollectionActivated() {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(repository.findById(43L)).thenReturn(Optional.of(order));

        CleaningOrder result = service.markAwaitingReport(43L, CLEANER_ID);

        Assertions.assertSame(order, result);
        Mockito.verify(order).requireCanStartReport(CLEANER_ID);
        Mockito.verify(repository).deactivateOtherReportInputs(CLEANER_ID, 43L);
        Mockito.verify(order).startReportCollection(CLEANER_ID);
    }

    @Test
    void activeReport_photoAndCaptionStored_idempotently() {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(order.getId()).thenReturn(43L);
        Mockito.when(order.getCleanerComment()).thenReturn("Looks good");
        Mockito.when(repository.findByCleanerTelegramUserIdAndReportInputActiveTrue(CLEANER_ID))
                .thenReturn(Optional.of(order));
        Mockito.when(mediaProviderReferenceService.resolveOrStore(
                Mockito.any(MediaUpload.class),
                Mockito.eq(MediaProvider.TELEGRAM),
                Mockito.eq("file-1"),
                Mockito.eq("unique-1")
        )).thenReturn(providerMedia(71L, "file-1", "unique-1"));
        Mockito.when(photoRepository.existsByOrderIdAndMediaAssetId(43L, 71L))
                .thenReturn(false);
        Mockito.when(photoRepository.countByOrderId(43L)).thenReturn(1L);

        CleaningOrderReportProgress progress = service.addPhotoToActiveReport(
                CLEANER_ID,
                "file-1",
                "unique-1",
                JPEG,
                " Looks good "
        );

        var photoCaptor = ArgumentCaptor.forClass(CleaningOrderPhoto.class);
        Mockito.verify(photoRepository).save(photoCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(71L, photoCaptor.getValue().getMediaAssetId()),
                () -> Assertions.assertEquals(43L, progress.orderId()),
                () -> Assertions.assertEquals(1L, progress.photoCount()),
                () -> Assertions.assertTrue(progress.commentPresent())
        );
        Mockito.verify(order).requireReportAccess(CLEANER_ID);
        Mockito.verify(order).updateCleanerComment(CLEANER_ID, "Looks good");
    }

    @Test
    void activeReport_unsupportedPhotoRejectedBeforeStorage() {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(repository.findByCleanerTelegramUserIdAndReportInputActiveTrue(CLEANER_ID))
                .thenReturn(Optional.of(order));

        Assertions.assertThrows(
                InvalidCompletionPhotoException.class,
                () -> service.addPhotoToActiveReport(
                        CLEANER_ID,
                        "file-1",
                        "unique-1",
                        new byte[]{1, 2, 3},
                        null
                )
        );

        Mockito.verifyNoInteractions(mediaProviderReferenceService, photoRepository);
    }

    @Test
    void reportWithoutPhotos_deliveryRejected() {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(repository.findById(43L)).thenReturn(Optional.of(order));
        Mockito.when(photoRepository.findAllByOrderIdOrderByCreatedAt(43L)).thenReturn(List.of());

        Assertions.assertThrows(
                PhotoReportEmptyException.class,
                () -> service.getReportForDelivery(43L, CLEANER_ID)
        );
        Mockito.verify(order).requireReportAccess(CLEANER_ID);
    }

    @Test
    void firstCompletedOrder_referralUnlockEventPublished() {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(order.getId()).thenReturn(43L);
        Mockito.when(order.getCustomerId()).thenReturn(77L);
        Mockito.when(order.getCommunicationIdentityId()).thenReturn(88L);
        Mockito.when(order.getStatus()).thenReturn(CleaningOrderStatus.AWAITING_REPORT);
        Mockito.when(repository.findById(43L)).thenReturn(Optional.of(order));
        Mockito.when(repository.existsByCustomerIdAndStatus(77L, CleaningOrderStatus.COMPLETED))
                .thenReturn(false);
        Mockito.when(referralService.completeOrder(order)).thenReturn("ALEX7K2");

        service.completeOrder(43L, CLEANER_ID, "Done");

        var eventCaptor = ArgumentCaptor.forClass(ReferralUnlockedEvent.class);
        Mockito.verify(eventPublisher).publishEvent(
                new CleaningOrderCustomerEvent.Completed(43L, 77L, 88L)
        );
        Mockito.verify(eventPublisher).publishEvent(eventCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(77L, eventCaptor.getValue().customerId()),
                () -> Assertions.assertEquals(88L, eventCaptor.getValue().communicationIdentityId()),
                () -> Assertions.assertEquals("ALEX7K2", eventCaptor.getValue().referralCode())
        );
    }

    @Test
    void laterCompletedOrder_referralUnlockEventNotPublishedAgain() {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(order.getId()).thenReturn(43L);
        Mockito.when(order.getCustomerId()).thenReturn(77L);
        Mockito.when(order.getCommunicationIdentityId()).thenReturn(88L);
        Mockito.when(order.getStatus()).thenReturn(CleaningOrderStatus.AWAITING_REPORT);
        Mockito.when(repository.findById(43L)).thenReturn(Optional.of(order));
        Mockito.when(repository.existsByCustomerIdAndStatus(77L, CleaningOrderStatus.COMPLETED))
                .thenReturn(true);
        Mockito.when(referralService.completeOrder(order)).thenReturn("ALEX7K2");

        service.completeOrder(43L, CLEANER_ID, null);

        Mockito.verify(eventPublisher).publishEvent(
                new CleaningOrderCustomerEvent.Completed(43L, 77L, 88L)
        );
        Mockito.verify(eventPublisher, Mockito.never())
                .publishEvent(Mockito.isA(ReferralUnlockedEvent.class));
    }

    private static StoredProviderMedia providerMedia(
            long mediaId,
            String externalId,
            String externalUniqueId
    ) {
        var stored = new StoredMedia(
                mediaId,
                "image/jpeg",
                JPEG.length,
                "a".repeat(64),
                NOW
        );
        var reference = new MediaProviderReferenceData(
                mediaId,
                MediaProvider.TELEGRAM,
                externalId,
                externalUniqueId,
                NOW
        );
        return new StoredProviderMedia(stored, reference);
    }

    private static CleaningOrder sampleOrder() {
        return new CleaningOrder(
                77L,
                88L,
                "Alex",
                "+90 555",
                ServiceArea.MAHMUTLAR,
                "Address",
                ApartmentType.TWO_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                organicSnapshot(BigDecimal.valueOf(1100)),
                null,
                null,
                null,
                null,
                "TRY",
                LocalDate.of(2026, 8, 18),
                null,
                NOW
        );
    }

    private static CreateCleaningOrderCommand validCommand(String phone) {
        return new CreateCleaningOrderCommand(
                ServiceArea.MAHMUTLAR,
                "Barbaros Cd. 24",
                ApartmentType.TWO_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                LocalDate.of(2026, 8, 18),
                phone,
                null,
                null
        );
    }

    private static OrderFinancialSnapshot organicSnapshot(BigDecimal basePrice) {
        BigDecimal commission = basePrice.multiply(new BigDecimal("0.15")).setScale(2);
        return new OrderFinancialSnapshot(
                basePrice.setScale(2),
                new BigDecimal("0.15"),
                commission,
                BigDecimal.ZERO.setScale(2),
                BigDecimal.ZERO.setScale(2),
                basePrice.setScale(2),
                commission,
                AcquisitionSource.ORGANIC,
                CustomerDiscountType.NONE
        );
    }

    private static CleaningProperties properties() {
        var regular = new CleaningProperties.PriceGroup(
                amount(800), amount(900), amount(1100), amount(1350), amount(1650), amount(300)
        );
        var deep = new CleaningProperties.PriceGroup(
                amount(1200), amount(1400), amount(1700), amount(2050), amount(2450), amount(450)
        );
        return new CleaningProperties(
                7,
                Currency.getInstance("TRY"),
                ZoneId.of("Europe/Istanbul"),
                new CleaningProperties.Prices(regular, deep)
        );
    }

    private static BigDecimal amount(long value) {
        return BigDecimal.valueOf(value);
    }
}
