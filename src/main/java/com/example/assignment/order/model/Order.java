package com.example.assignment.order.model;

import com.example.assignment.order.exception.InvalidOrderStateException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class Order {

    private final Long id;
    private final Long customerId;
    private final List<OrderItem> items;
    private final BigDecimal totalAmount;
    private final Instant createdAt;

    private volatile Instant updatedAt;
    private volatile OrderStatus status;

    public Order(
            Long id,
            Long customerId,
            List<OrderItem> items,
            BigDecimal totalAmount
    ) {
        this.id = id;
        this.customerId = customerId;
        this.items = List.copyOf(items);
        this.totalAmount = totalAmount;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    /**
     * State transition is synchronized because cancellation,
     * scheduled processing and manual status updates can happen
     * concurrently.
     */
    public synchronized void transitionTo(OrderStatus target) {

        if (status == target) {
            throw new InvalidOrderStateException(
                    "Order " + id +
                            " is already in " + status + " state"
            );
        }

        if (!status.canTransitionTo(target)) {
            throw new InvalidOrderStateException(
                    "Cannot transition order " + id +
                            " from " + status +
                            " to " + target
            );
        }

        this.status = target;
        this.updatedAt = Instant.now();
    }
}
