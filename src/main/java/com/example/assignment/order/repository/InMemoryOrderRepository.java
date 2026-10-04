package com.example.assignment.order.repository;

import com.example.assignment.order.model.Order;
import com.example.assignment.order.model.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryOrderRepository {

    private final ConcurrentMap<Long, Order> orders =
            new ConcurrentHashMap<>();

    private final AtomicLong idGenerator =
            new AtomicLong(1000);

    public Order save(Order order) {

        orders.put(order.getId(), order);

        return order;
    }

    public Long generateId() {
        return idGenerator.incrementAndGet();
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    public List<Order> findByStatus(OrderStatus status) {

        return orders.values()
                .stream()
                .filter(order -> order.getStatus() == status)
                .toList();
    }

    public int size() {
        return orders.size();
    }
}
