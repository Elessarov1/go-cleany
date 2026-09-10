package com.cleany.order;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cleany.reminder.CleaningRepeatReminderRequest;
import com.cleany.reminder.CleaningRepeatReminderResponse;
import com.cleany.reminder.CleaningRepeatReminderService;
import com.cleany.idempotency.IdempotencyService;
import com.cleany.pagination.CursorPageResponse;
import com.cleany.pagination.OpaqueCursorPagination;

@RestController
@RequestMapping(CleaningOrderController.BASE_PATH)
public class CleaningOrderController {

    static final String BASE_PATH = "/api/v1/cleaning/orders";

    private final CleaningOrderService orderService;
    private final CustomerCleaningReportService reportService;
    private final CleaningRepeatReminderService repeatReminderService;
    private final IdempotencyService idempotencyService;

    public CleaningOrderController(
            CleaningOrderService orderService,
            CustomerCleaningReportService reportService,
            CleaningRepeatReminderService repeatReminderService,
            IdempotencyService idempotencyService
    ) {
        this.orderService = orderService;
        this.reportService = reportService;
        this.repeatReminderService = repeatReminderService;
        this.idempotencyService = idempotencyService;
    }

    @PostMapping
    public ResponseEntity<CleaningOrderResponse> createOrder(
            @RequestHeader(name = "Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateCleaningOrderRequest request
    ) {
        var response = idempotencyService.execute(idempotencyKey, "CREATE_CLEANING_ORDER", request,
                "CLEANING_ORDER",
                () -> {
                    var order = orderService.createOrder(request.toCommand(), request.repeatFromOrderId());
                    return CleaningOrderResponse.from(order, reportService.summary(order));
                },
                CleaningOrderResponse::id,
                id -> {
                    var order = orderService.getCurrentCustomerOrder(id);
                    return CleaningOrderResponse.from(order, reportService.summary(order));
                });
        return ResponseEntity
                .created(URI.create(BASE_PATH + "/" + response.id()))
                .body(response);
    }

    @PostMapping("/quote")
    public CleaningOrderQuoteResponse quoteOrder(
            @Valid @RequestBody CleaningOrderQuoteRequest request
    ) {
        return orderService.quoteOrder(request);
    }

    @GetMapping
    public CursorPageResponse<CleaningOrderResponse> getOrders(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer size
    ) {
        return OpaqueCursorPagination.descending(orderService.getCurrentCustomerOrders(), cursor, size,
                CleaningOrder::getCreatedAt, CleaningOrder::getId,
                order -> CleaningOrderResponse.from(order, reportService.summary(order)));
    }

    @GetMapping("/{id}")
    public CleaningOrderResponse getOrder(@PathVariable long id) {
        CleaningOrder order = orderService.getCurrentCustomerOrder(id);
        return CleaningOrderResponse.from(order, reportService.summary(order));
    }

    @PostMapping("/{id}/repeat-shown")
    public ResponseEntity<Void> recordRepeatShown(@PathVariable long id) {
        orderService.recordRepeatShown(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/repeat-prefill")
    public CleaningRepeatPrefillResponse repeatPrefill(@PathVariable long id) {
        return orderService.repeatPrefill(id);
    }

    @GetMapping("/{id}/repeat-reminder")
    public CleaningRepeatReminderResponse repeatReminder(@PathVariable long id) {
        return repeatReminderService.current(id);
    }

    @PutMapping("/{id}/repeat-reminder")
    public CleaningRepeatReminderResponse updateRepeatReminder(
            @PathVariable long id,
            @Valid @RequestBody CleaningRepeatReminderRequest request
    ) {
        return repeatReminderService.update(id, request.selection());
    }

    @PostMapping("/{id}/cancel")
    public CleaningOrderResponse cancelOrder(@PathVariable long id) {
        CleaningOrder order = orderService.cancelCurrentCustomerOrder(id);
        return CleaningOrderResponse.from(order, reportService.summary(order));
    }

    @GetMapping("/{id}/report")
    public CustomerCleaningReportResponse getReport(@PathVariable long id) {
        return reportService.currentCustomerReport(id);
    }

    @GetMapping("/{id}/report/photos/{mediaId}")
    public ResponseEntity<byte[]> getReportPhoto(
            @PathVariable long id,
            @PathVariable long mediaId
    ) {
        CustomerCleaningReportPhotoContent photo = reportService.currentCustomerPhoto(id, mediaId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.contentType()))
                .cacheControl(CacheControl.noStore())
                .body(photo.content());
    }
}
