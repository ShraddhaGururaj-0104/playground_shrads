package com.example.assignment;

import com.example.assignment.order.dto.CreateOrderRequest;
import com.example.assignment.order.dto.OrderItemRequest;
import com.example.assignment.order.dto.OrderResponse;
import com.example.assignment.order.dto.UpdateOrderStatusRequest;
import com.example.assignment.order.model.OrderStatus;
import com.example.assignment.order.service.OrderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class AssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssignmentApplication.class, args);
	}

	@Bean
	CommandLineRunner testFlow(OrderService orderService) {
		return args -> {

			System.out.println("\n========== 1. CREATE ORDER ==========");

			CreateOrderRequest createRequest = new CreateOrderRequest(
					101L,
					List.of(
							new OrderItemRequest(
									1L,
									2,
									new BigDecimal("499.99")
							),
							new OrderItemRequest(
									2L,
									1,
									new BigDecimal("199.99")
							)
					)
			);

			OrderResponse order =
					orderService.createOrder(createRequest);

			System.out.println("Order ID      : " + order.orderId());
			System.out.println("Customer ID   : " + order.customerId());
			System.out.println("Status        : " + order.status());
			System.out.println("Total Amount  : " + order.totalAmount());


			System.out.println("\n========== 2. GET ORDER BY ID ==========");

			OrderResponse fetched =
					orderService.getOrder(order.orderId());

			System.out.println(fetched);


			System.out.println("\n========== 3. LIST ALL ORDERS ==========");

			List<OrderResponse> allOrders =
					orderService.getOrders(null);

			allOrders.forEach(System.out::println);


			System.out.println("\n========== 4. LIST PENDING ORDERS ==========");

			List<OrderResponse> pendingOrders =
					orderService.getOrders(OrderStatus.PENDING);

			pendingOrders.forEach(System.out::println);


			System.out.println("\n========== 5. PROCESS PENDING ORDER ==========");

			int processed =
					orderService.processPendingOrders();

			System.out.println("Processed orders: " + processed);

			OrderResponse processingOrder =
					orderService.getOrder(order.orderId());

			System.out.println(
					"New status: " + processingOrder.status()
			);


			System.out.println("\n========== 6. SHIP ORDER ==========");

			OrderResponse shipped =
					orderService.updateStatus(
							order.orderId(),
							new UpdateOrderStatusRequest(
									OrderStatus.SHIPPED
							)
					);

			System.out.println(
					"Status: " + shipped.status()
			);


			System.out.println("\n========== 7. DELIVER ORDER ==========");

			OrderResponse delivered =
					orderService.updateStatus(
							order.orderId(),
							new UpdateOrderStatusRequest(
									OrderStatus.DELIVERED
							)
					);

			System.out.println(
					"Status: " + delivered.status()
			);


			System.out.println("\n========== 8. TEST CANCELLATION ==========");

			CreateOrderRequest secondRequest =
					new CreateOrderRequest(
							102L,
							List.of(
									new OrderItemRequest(
											3L,
											1,
											new BigDecimal("999.99")
									)
							)
					);

			OrderResponse secondOrder =
					orderService.createOrder(secondRequest);

			System.out.println(
					"Second order status: " +
							secondOrder.status()
			);

			OrderResponse cancelled =
					orderService.cancelOrder(
							secondOrder.orderId()
					);

			System.out.println(
					"After cancellation: " +
							cancelled.status()
			);


			System.out.println("\n========== 9. VERIFY CANCELLED ORDER IS NOT PROCESSED ==========");

			orderService.processPendingOrders();

			OrderResponse finalCancelledOrder =
					orderService.getOrder(
							secondOrder.orderId()
					);

			System.out.println(
					"Cancelled order status: " +
							finalCancelledOrder.status()
			);


			System.out.println("\n========== TEST COMPLETE ==========");
		};

	}

}
