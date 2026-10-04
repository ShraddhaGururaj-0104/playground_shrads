package com.example.assignment.order.dto;

import com.example.assignment.order.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(

        @NotNull(message = "status is required")
        OrderStatus status

) {
}