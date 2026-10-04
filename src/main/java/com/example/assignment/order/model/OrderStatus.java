package com.example.assignment.order.model;

public enum OrderStatus {

    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus target) {

        return switch (this) {
            case PENDING ->
                    target == PROCESSING ||
                            target == CANCELLED;

            case PROCESSING ->
                    target == SHIPPED;

            case SHIPPED ->
                    target == DELIVERED;

            case DELIVERED, CANCELLED ->
                    false;
        };
    }
}