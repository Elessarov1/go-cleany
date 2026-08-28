package com.cleany.order;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CleaningOrderController.BASE_PATH)
public class CleaningOrderController {

    static final String BASE_PATH = "/api/v1/cleaning/orders";

    private final CleaningOrderService orderService;
    private final CustomerCleaningReportService reportService;

    public CleaningOrderController(
            CleaningOrderService orderService,
            CustomerCleaningReportService reportService
    ) {
        this.orderService = orderService;
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<CleaningOrderResponse> createOrder(
            @Valid @RequestBody CreateCleaningOrderRequest request
    ) {
        var order = orderService.createOrder(request.toCommand());
        var response = CleaningOrderResponse.from(order, reportService.summary(order));
        return ResponseEntity
                .created(URI.create(BASE_PATH + "/" + order.getId()))
                .body(response);
    }

    @PostMapping("/quote")
    public CleaningOrderQuoteResponse quoteOrder(
            @Valid @RequestBody CleaningOrderQuoteRequest request
    ) {
        return orderService.quoteOrder(request);
    }

    @GetMapping
    public List<CleaningOrderResponse> getOrders() {
        return orderService.getCurrentCustomerOrders().stream()
                .map(order -> CleaningOrderResponse.from(order, reportService.summary(order)))
                .toList();
    }

    @GetMapping("/{id}")
    public CleaningOrderResponse getOrder(@PathVariable long id) {
        CleaningOrder order = orderService.getCurrentCustomerOrder(id);
        return CleaningOrderResponse.from(order, reportService.summary(order));
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
                .body(photo.content());
    }
}
