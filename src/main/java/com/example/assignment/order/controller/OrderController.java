package com.example.assignment.order.controller;

import com.example.assignment.order.dto.CreateOrderRequest;
import com.example.assignment.order.dto.OrderResponse;
import com.example.assignment.order.dto.UpdateOrderStatusRequest;
import com.example.assignment.order.model.OrderStatus;
import com.example.assignment.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {

        return orderService.createOrder(request);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            @PathVariable Long orderId
    ) {

        return orderService.getOrder(orderId);
    }

    @GetMapping
    public List<OrderResponse> getOrders(
            @RequestParam(required = false) OrderStatus status
    ) {

        return orderService.getOrders(status);
    }

    @PatchMapping("/{orderId}/status")
    public OrderResponse updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {

        return orderService.updateStatus(
                orderId,
                request
        );
    }

    @PostMapping("/{orderId}/cancel")
    public OrderResponse cancelOrder(
            @PathVariable Long orderId
    ) {

        return orderService.cancelOrder(orderId);
    }
}
