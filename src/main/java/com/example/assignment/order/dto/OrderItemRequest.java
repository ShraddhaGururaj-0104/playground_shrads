package com.example.assignment.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderItemRequest(

        @NotNull(message = "productId is required")
        Long productId,

        @Min(value = 1, message = "quantity must be at least 1")
        int quantity,

        @NotNull(message = "unitPrice is required")
        @DecimalMin(
                value = "0.01",
                message = "unitPrice must be greater than zero"
        )
        BigDecimal unitPrice

) {
}
