package com.cleany.order;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class CleaningOrderController {

    private final CleaningOrderService orderService;

    public CleaningOrderController(CleaningOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<CleaningOrderResponse> createOrder(
            @Valid @RequestBody CreateCleaningOrderRequest request
    ) {
        var order = orderService.createOrder(request.toCommand());
        var response = CleaningOrderResponse.from(order);
        return ResponseEntity
                .created(URI.create("/api/v1/orders/" + order.getId()))
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
                .map(CleaningOrderResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public CleaningOrderResponse getOrder(@PathVariable long id) {
        return CleaningOrderResponse.from(orderService.getCurrentCustomerOrder(id));
    }

    @PostMapping("/{id}/cancel")
    public CleaningOrderResponse cancelOrder(@PathVariable long id) {
        return CleaningOrderResponse.from(orderService.cancelCurrentCustomerOrder(id));
    }
}
