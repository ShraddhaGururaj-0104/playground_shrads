package com.example.assignment.order;

import com.example.assignment.order.dto.CreateOrderRequest;
import com.example.assignment.order.dto.OrderItemRequest;
import com.example.assignment.order.dto.OrderResponse;
import com.example.assignment.order.dto.UpdateOrderStatusRequest;
import com.example.assignment.order.exception.InvalidOrderStateException;
import com.example.assignment.order.exception.OrderNotFoundException;
import com.example.assignment.order.model.OrderStatus;
import com.example.assignment.order.repository.InMemoryOrderRepository;
import com.example.assignment.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private InMemoryOrderRepository repository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();
        orderService = new OrderService(repository);
    }

    @Test
    void shouldCreateOrderWithMultipleItems() {

        CreateOrderRequest request =
                new CreateOrderRequest(
                        101L,
                        List.of(
                                new OrderItemRequest(
                                        1L,
                                        2,
                                        new BigDecimal("10.00")
                                ),
                                new OrderItemRequest(
                                        2L,
                                        1,
                                        new BigDecimal("25.00")
                                )
                        )
                );

        OrderResponse response =
                orderService.createOrder(request);

        assertNotNull(response.orderId());
        assertEquals(101L, response.customerId());

        assertEquals(
                OrderStatus.PENDING,
                response.status()
        );

        assertEquals(
                new BigDecimal("45.00"),
                response.totalAmount()
        );

        assertEquals(2, response.items().size());
    }

    @Test
    void shouldRetrieveOrder() {

        OrderResponse created =
                createSampleOrder();

        OrderResponse retrieved =
                orderService.getOrder(created.orderId());

        assertEquals(
                created.orderId(),
                retrieved.orderId()
        );
    }

    @Test
    void shouldThrowWhenOrderDoesNotExist() {

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrder(99999L)
        );
    }

    @Test
    void shouldCancelPendingOrder() {

        OrderResponse created =
                createSampleOrder();

        OrderResponse cancelled =
                orderService.cancelOrder(
                        created.orderId()
                );

        assertEquals(
                OrderStatus.CANCELLED,
                cancelled.status()
        );
    }

    @Test
    void shouldNotCancelProcessingOrder() {

        OrderResponse created =
                createSampleOrder();

        orderService.updateStatus(
                created.orderId(),
                new UpdateOrderStatusRequest(
                        OrderStatus.PROCESSING
                )
        );

        assertThrows(
                InvalidOrderStateException.class,
                () -> orderService.cancelOrder(
                        created.orderId()
                )
        );
    }

    @Test
    void shouldProcessPendingOrders() {

        createSampleOrder();
        createSampleOrder();

        int processed =
                orderService.processPendingOrders();

        assertEquals(2, processed);

        assertEquals(
                2,
                orderService.getOrders(
                        OrderStatus.PROCESSING
                ).size()
        );
    }

    @Test
    void shouldNotAllowInvalidStateTransition() {

        OrderResponse created =
                createSampleOrder();

        assertThrows(
                InvalidOrderStateException.class,
                () -> orderService.updateStatus(
                        created.orderId(),
                        new UpdateOrderStatusRequest(
                                OrderStatus.DELIVERED
                        )
                )
        );
    }

    @Test
    void shouldMoveOrderThroughValidLifecycle() {

        OrderResponse order =
                createSampleOrder();

        orderService.updateStatus(
                order.orderId(),
                new UpdateOrderStatusRequest(
                        OrderStatus.PROCESSING
                )
        );

        orderService.updateStatus(
                order.orderId(),
                new UpdateOrderStatusRequest(
                        OrderStatus.SHIPPED
                )
        );

        OrderResponse delivered =
                orderService.updateStatus(
                        order.orderId(),
                        new UpdateOrderStatusRequest(
                                OrderStatus.DELIVERED
                        )
                );

        assertEquals(
                OrderStatus.DELIVERED,
                delivered.status()
        );
    }

    @Test
    void shouldHandleConcurrentCancellationAndProcessing() throws Exception {

        OrderResponse created = createSampleOrder();

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        Future<?> cancellation =
                executor.submit(() ->
                        orderService.cancelOrder(
                                created.orderId()
                        )
                );

        Future<?> processing =
                executor.submit(() ->
                        orderService.processPendingOrders()
                );

        try {
            cancellation.get();
        } catch (ExecutionException ignored) {
            // One operation may legitimately lose the race.
        }

        try {
            processing.get();
        } catch (ExecutionException ignored) {
            // One operation may legitimately lose the race.
        }

        executor.shutdown();

        OrderResponse finalOrder =
                orderService.getOrder(created.orderId());

        assertTrue(
                finalOrder.status() == OrderStatus.CANCELLED ||
                        finalOrder.status() == OrderStatus.PROCESSING
        );
    }

    private OrderResponse createSampleOrder() {

        CreateOrderRequest request =
                new CreateOrderRequest(
                        101L,
                        List.of(
                                new OrderItemRequest(
                                        1L,
                                        2,
                                        new BigDecimal("10.00")
                                )
                        )
                );

        return orderService.createOrder(request);
    }
}
