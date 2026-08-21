package com.cleany.order;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HexFormat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.CustomerIdentityTestFixture;
import com.cleany.finance.AcquisitionSource;
import com.cleany.finance.CustomerDiscountType;
import com.cleany.finance.OrderFinancialSnapshot;
import com.cleany.telegram.TelegramInitDataTestFactory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class OnsiteIssueIntegrationTest extends BaseIntegrationTest {

    private static final long CLEANER_ID = 123456789L;
    private static final long OTHER_CLEANER_ID = 987654321L;
    private static final long ADMIN_ID = 900001L;
    private static final String BOT_TOKEN = "123456789:test-token";
    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xD9};

    @Autowired
    private CleaningOrderRepository orderRepository;

    @Autowired
    private CleaningOrderIssueReportRepository issueReportRepository;

    @Autowired
    private CleaningOrderIssuePhotoRepository issuePhotoRepository;

    @Autowired
    private CleaningOrderPhotoRepository completionPhotoRepository;

    @Autowired
    private CleaningOrderEventRepository eventRepository;

    @Autowired
    private CustomerAccountRepository customerAccountRepository;

    @Autowired
    private CustomerExternalIdentityRepository customerIdentityRepository;

    @Autowired
    private CleaningOrderService orderService;

    @Autowired
    private OnsiteIssueService onsiteIssueService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void cleanDatabase() {
        issuePhotoRepository.deleteAll();
        issueReportRepository.deleteAll();
        completionPhotoRepository.deleteAll();
        eventRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void onlyAssignedCleanerCanStartAndReasonIsRequired() {
        CleaningOrder order = acceptedOrder();

        Assertions.assertDoesNotThrow(() -> onsiteIssueService.start(order.getId(), CLEANER_ID));
        Assertions.assertThrows(
                CleanerNotAuthorizedException.class,
                () -> onsiteIssueService.start(order.getId(), OTHER_CLEANER_ID)
        );
        InvalidOnsiteIssueException invalidReason = Assertions.assertThrows(
                InvalidOnsiteIssueException.class,
                () -> onsiteIssueService.selectReason(order.getId(), CLEANER_ID, null)
        );
        Assertions.assertEquals(OnsiteIssueProblem.REASON_REQUIRED, invalidReason.getProblem());

        onsiteIssueService.selectReason(order.getId(), CLEANER_ID, OnsiteIssueReason.OTHER);
        Assertions.assertTrue(onsiteIssueService.hasActiveDraft(CLEANER_ID));
        orderService.markAwaitingReport(order.getId(), CLEANER_ID);
        Assertions.assertFalse(onsiteIssueService.hasActiveDraft(CLEANER_ID));
    }

    @Test
    void evidenceValidationRejectsIncompleteOversizedUnsupportedAndExcessReports() {
        CleaningOrder order = acceptedOrder();
        onsiteIssueService.selectReason(order.getId(), CLEANER_ID, OnsiteIssueReason.HEAVY_CONTAMINATION);

        assertProblem(
                OnsiteIssueProblem.COMMENT_REQUIRED,
                () -> onsiteIssueService.submit(order.getId(), CLEANER_ID)
        );
        onsiteIssueService.updateComment(CLEANER_ID, "Сильное загрязнение не соответствует заявке");
        assertProblem(
                OnsiteIssueProblem.MIN_PHOTOS_REQUIRED,
                () -> onsiteIssueService.submit(order.getId(), CLEANER_ID)
        );
        assertProblem(
                OnsiteIssueProblem.PHOTO_TYPE_UNSUPPORTED,
                () -> onsiteIssueService.addPhoto(
                        CLEANER_ID,
                        "unsupported-file",
                        "unsupported-unique",
                        new byte[]{1, 2, 3, 4},
                        null
                )
        );

        byte[] oversized = new byte[5 * 1024 * 1024 + 1];
        System.arraycopy(JPEG, 0, oversized, 0, JPEG.length);
        assertProblem(
                OnsiteIssueProblem.PHOTO_TOO_LARGE,
                () -> onsiteIssueService.addPhoto(
                        CLEANER_ID,
                        "oversized-file",
                        "oversized-unique",
                        oversized,
                        null
                )
        );

        for (int index = 0; index < 8; index++) {
            onsiteIssueService.addPhoto(
                    CLEANER_ID,
                    "file-" + index,
                    "unique-" + index,
                    JPEG,
                    null
            );
        }
        assertProblem(
                OnsiteIssueProblem.MAX_PHOTOS_EXCEEDED,
                () -> onsiteIssueService.addPhoto(
                        CLEANER_ID,
                        "file-9",
                        "unique-9",
                        JPEG,
                        null
                )
        );
        Assertions.assertEquals(CleaningOrderStatus.ACCEPTED, reloaded(order).getStatus());
    }

    @Test
    void submitPersistsBinaryMetadataDeduplicatesAndBlocksCompletion() throws Exception {
        CleaningOrder order = acceptedOrder();
        onsiteIssueService.selectReason(order.getId(), CLEANER_ID, OnsiteIssueReason.ADDRESS_MISMATCH);

        OnsiteIssueProgress first = onsiteIssueService.addPhoto(
                CLEANER_ID,
                "file-1",
                "unique-1",
                JPEG,
                null
        );
        OnsiteIssueProgress duplicate = onsiteIssueService.addPhoto(
                CLEANER_ID,
                "file-1-new-reference",
                "unique-1",
                JPEG,
                null
        );
        onsiteIssueService.addPhoto(CLEANER_ID, "file-2", "unique-2", JPEG, null);
        onsiteIssueService.addPhoto(CLEANER_ID, "file-3", "unique-3", JPEG, null);
        onsiteIssueService.updateComment(CLEANER_ID, "Адрес и фактический объект не совпадают");
        onsiteIssueService.submit(order.getId(), CLEANER_ID);

        CleaningOrderIssueReport report = issueReportRepository.findByOrder_Id(order.getId()).orElseThrow();
        var photos = issuePhotoRepository.findAllByIssueReport_IdOrderByCreatedAtAscIdAsc(report.getId());
        CleaningOrderIssuePhoto firstPhoto = photos.getFirst();
        var events = eventRepository.findAllByOrderIdOrderByOccurredAtAscIdAsc(order.getId());

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, first.photoCount()),
                () -> Assertions.assertEquals(1L, duplicate.photoCount()),
                () -> Assertions.assertEquals(3, photos.size()),
                () -> Assertions.assertArrayEquals(JPEG, firstPhoto.getContent()),
                () -> Assertions.assertEquals("image/jpeg", firstPhoto.getContentType()),
                () -> Assertions.assertEquals(JPEG.length, firstPhoto.getSizeBytes()),
                () -> Assertions.assertEquals(sha256(JPEG), firstPhoto.getSha256()),
                () -> Assertions.assertNotNull(report.getSubmittedAt()),
                () -> Assertions.assertEquals(
                        CleaningOrderStatus.ONSITE_ISSUE_REPORTED,
                        reloaded(order).getStatus()
                ),
                () -> Assertions.assertTrue(events.stream().anyMatch(
                        event -> event.getEventType() == OrderEventType.ONSITE_ISSUE_REPORTED
                )),
                () -> Assertions.assertTrue(events.stream().anyMatch(
                        event -> event.getEventType() == OrderEventType.ISSUE_REPORT_SUBMITTED
                ))
        );

        Assertions.assertThrows(
                InvalidOrderStateException.class,
                () -> orderService.completeOrder(order.getId(), CLEANER_ID, "Нельзя завершить")
        );
    }

    @Test
    void adminCanReadDownloadAndResolveWhileNonAdminCannotAccessEvidence() throws Exception {
        CleaningOrder order = submittedIssue();
        CleaningOrderIssueReport report = issueReportRepository.findByOrder_Id(order.getId()).orElseThrow();
        long photoId = issuePhotoRepository
                .findAllByIssueReport_IdOrderByCreatedAtAscIdAsc(report.getId())
                .getFirst()
                .getId();
        String adminAuth = authorization(ADMIN_ID, "Admin");
        String userAuth = authorization(900002L, "Customer");
        String photoUrl = "/api/v1/admin/orders/" + order.getId() + "/issues/photos/" + photoId;

        mvc.perform(get("/api/v1/admin/orders/{id}", order.getId())
                        .header("Authorization", adminAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onsiteIssue.reason").value("HEAVY_CONTAMINATION"))
                .andExpect(jsonPath("$.onsiteIssue.comment").value("Фактическое состояние требует других условий"))
                .andExpect(jsonPath("$.onsiteIssue.photos[0].id").value(photoId))
                .andExpect(jsonPath("$.onsiteIssue.photos[0].contentType").value("image/jpeg"))
                .andExpect(jsonPath("$.onsiteIssue.photos[0].sizeBytes").value(JPEG.length));

        mvc.perform(get(photoUrl).header("Authorization", adminAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(header().longValue("Content-Length", JPEG.length))
                .andExpect(content().bytes(JPEG));

        mvc.perform(get(photoUrl).header("Authorization", userAuth))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("admin_not_authorized"));

        mvc.perform(post("/api/v1/admin/orders/{id}/issues/resolve", order.getId())
                        .header("Authorization", adminAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"resolutionComment":"Инцидент проверен, заказ закрыт администратором"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order.status").value("CANCELLED"))
                .andExpect(jsonPath("$.onsiteIssue.resolvedBy").value(ADMIN_ID))
                .andExpect(jsonPath("$.onsiteIssue.resolutionComment")
                        .value("Инцидент проверен, заказ закрыт администратором"));

        CleaningOrderIssueReport resolved = issueReportRepository.findByOrder_Id(order.getId()).orElseThrow();
        Assertions.assertAll(
                () -> Assertions.assertEquals(CleaningOrderStatus.CANCELLED, reloaded(order).getStatus()),
                () -> Assertions.assertNotNull(resolved.getResolvedAt()),
                () -> Assertions.assertEquals(ADMIN_ID, resolved.getResolvedBy()),
                () -> Assertions.assertEquals(3L, issuePhotoRepository.countByIssueReport_Id(report.getId()))
        );

        mvc.perform(get(photoUrl).header("Authorization", adminAuth))
                .andExpect(status().isOk())
                .andExpect(content().bytes(JPEG));
    }

    private CleaningOrder submittedIssue() {
        CleaningOrder order = acceptedOrder();
        onsiteIssueService.selectReason(order.getId(), CLEANER_ID, OnsiteIssueReason.HEAVY_CONTAMINATION);
        onsiteIssueService.addPhoto(CLEANER_ID, "file-1", "unique-1", JPEG, null);
        onsiteIssueService.addPhoto(CLEANER_ID, "file-2", "unique-2", JPEG, null);
        onsiteIssueService.addPhoto(CLEANER_ID, "file-3", "unique-3", JPEG, null);
        onsiteIssueService.updateComment(CLEANER_ID, "Фактическое состояние требует других условий");
        onsiteIssueService.submit(order.getId(), CLEANER_ID);
        onsiteIssueService.recordCustomerNotified(order.getId(), CLEANER_ID);
        return reloaded(order);
    }

    private CleaningOrder acceptedOrder() {
        var customer = CustomerIdentityTestFixture.telegramIdentity(
                customerAccountRepository,
                customerIdentityRepository,
                Instant.now()
        );
        BigDecimal basePrice = BigDecimal.valueOf(1100);
        BigDecimal commission = basePrice.multiply(new BigDecimal("0.15")).setScale(2);
        CleaningOrder order = orderRepository.save(new CleaningOrder(
                customer.customerId(),
                customer.externalIdentityId(),
                "Alex",
                "+90 555 123 45 67",
                ServiceArea.MAHMUTLAR,
                "Barbaros Cd. 24",
                ApartmentType.TWO_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                new OrderFinancialSnapshot(
                        basePrice.setScale(2), new BigDecimal("0.15"), commission,
                        BigDecimal.ZERO.setScale(2), BigDecimal.ZERO.setScale(2), basePrice.setScale(2), commission,
                        AcquisitionSource.ORGANIC, CustomerDiscountType.NONE
                ),
                null,
                null,
                null,
                null,
                "TRY",
                LocalDate.now().plusDays(1),
                null,
                Instant.now()
        ));
        return orderService.acceptOrder(order.getId(), CLEANER_ID);
    }

    private CleaningOrder reloaded(CleaningOrder order) {
        return orderRepository.findById(order.getId()).orElseThrow();
    }

    private static void assertProblem(OnsiteIssueProblem expected, Executable executable) {
        InvalidOnsiteIssueException exception = Assertions.assertThrows(
                InvalidOnsiteIssueException.class,
                executable
        );
        Assertions.assertEquals(expected, exception.getProblem());
    }

    private static String sha256(byte[] value) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value));
    }

    private static String authorization(long userId, String name) {
        String userJson = """
                {"id":%d,"first_name":"%s","username":"user%d"}
                """.formatted(userId, name, userId).strip();
        return "tma " + TelegramInitDataTestFactory.signed(BOT_TOKEN, Instant.now(), userJson);
    }
}
