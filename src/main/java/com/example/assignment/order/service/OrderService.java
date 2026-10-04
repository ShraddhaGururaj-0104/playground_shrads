package com.example.assignment.order.service;

import com.example.assignment.order.dto.*;
import com.example.assignment.order.exception.OrderNotFoundException;
import com.example.assignment.order.model.Order;
import com.example.assignment.order.model.OrderItem;
import com.example.assignment.order.model.OrderStatus;
import com.example.assignment.order.repository.InMemoryOrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final InMemoryOrderRepository repository;

    public OrderService(InMemoryOrderRepository repository) {
        this.repository = repository;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {

        List<OrderItem> items = request.items()
                .stream()
                .map(this::toOrderItem)
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(OrderItem::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long orderId = repository.generateId();

        Order order = new Order(
                orderId,
                request.customerId(),
                items,
                totalAmount
        );

        repository.save(order);

        return toResponse(order);
    }

    public OrderResponse getOrder(Long orderId) {

        Order order = getOrderOrThrow(orderId);

        return toResponse(order);
    }

    public List<OrderResponse> getOrders(OrderStatus status) {

        List<Order> orders = status == null
                ? repository.findAll()
                : repository.findByStatus(status);

        return orders.stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse updateStatus(
            Long orderId,
            UpdateOrderStatusRequest request
    ) {

        Order order = getOrderOrThrow(orderId);

        order.transitionTo(request.status());

        return toResponse(order);
    }

    public OrderResponse cancelOrder(Long orderId) {

        Order order = getOrderOrThrow(orderId);

        order.transitionTo(OrderStatus.CANCELLED);

        return toResponse(order);
    }

    public int processPendingOrders() {

        List<Order> pendingOrders =
                repository.findByStatus(OrderStatus.PENDING);

        int processedCount = 0;

        for (Order order : pendingOrders) {

            try {
                order.transitionTo(OrderStatus.PROCESSING);
                processedCount++;
            } catch (Exception ignored) {
                /*
                 * Another thread may have changed the order
                 * between findByStatus() and transitionTo().
                 *
                 * transitionTo() performs the actual atomic
                 * state validation.
                 */
            }
        }

        return processedCount;
    }

    private Order getOrderOrThrow(Long orderId) {

        return repository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(orderId));
    }

    private OrderItem toOrderItem(OrderItemRequest request) {

        return new OrderItem(
                request.productId(),
                request.quantity(),
                request.unitPrice()
        );
    }

    private OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(item -> new OrderItemResponse(
                                item.productId(),
                                item.quantity(),
                                item.unitPrice(),
                                item.totalPrice()
                        ))
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items
        );
    }
}
