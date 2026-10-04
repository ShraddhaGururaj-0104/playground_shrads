package com.example.assignment.order.dto;

import com.example.assignment.order.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(

        Long orderId,

        Long customerId,

        OrderStatus status,

        BigDecimal totalAmount,

        Instant createdAt,

        Instant updatedAt,

        List<OrderItemResponse> items

) {
}
