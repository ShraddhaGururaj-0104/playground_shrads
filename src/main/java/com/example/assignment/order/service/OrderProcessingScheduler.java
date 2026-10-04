package com.example.assignment.order.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessingScheduler {

    private static final long FIVE_MINUTES =
            5 * 60 * 1000L;

    private final OrderService orderService;

    public OrderProcessingScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = FIVE_MINUTES)
    public void processPendingOrders() {

        orderService.processPendingOrders();
    }
}
