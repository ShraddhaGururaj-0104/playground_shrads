package com.example.assignment.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(

        @NotNull(message = "customerId is required")
        Long customerId,

        @NotEmpty(message = "Order must contain at least one item")
        List<@Valid OrderItemRequest> items

) {
}
