package com.example.assignment.order.model;

import java.math.BigDecimal;

public record OrderItem(
        Long productId,
        int quantity,
        BigDecimal unitPrice
) {

    public BigDecimal totalPrice() {
        return unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }
}
