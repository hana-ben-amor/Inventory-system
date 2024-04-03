package com.example.orderservice.Service;

import com.example.orderservice.Entity.Order;
import com.example.orderservice.Entity.OrderStatus;
import com.example.orderservice.Repository.OrderRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    private final Counter ordersPendingCounter;
    private final Counter ordersProcessingCounter;
    private final Counter ordersShippedCounter;
    private final Counter ordersDeliveredCounter;
    private final Counter totalOrdersCounter;

    public OrderService(MeterRegistry meterRegistry) {
        ordersPendingCounter = Counter.builder("orders_pending")
                .description("Count of orders with status PENDING")
                .register(meterRegistry);

        ordersProcessingCounter = Counter.builder("orders_processing")
                .description("Count of orders with status PROCESSING")
                .register(meterRegistry);

        ordersShippedCounter = Counter.builder("orders_shipped")
                .description("Count of orders with status SHIPPED")
                .register(meterRegistry);

        ordersDeliveredCounter = Counter.builder("orders_delivered")
                .description("Count of orders with status DELIVERED")
                .register(meterRegistry);

        totalOrdersCounter = Counter.builder("total_orders")
                .description("Total number of orders")
                .register(meterRegistry);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public Order createOrder(Order order) {
        Order createdOrder = orderRepository.save(order);
        totalOrdersCounter.increment();
        processOrder(order); // Process the order to update status-based counters
        return createdOrder;
    }

    public Order updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        // Decrement the counter based on the existing status
        decrementCounter(existingOrder.getStatus());

        // Update the order
        existingOrder.setCustomerId(updatedOrder.getCustomerId());
        existingOrder.setOrderDate(updatedOrder.getOrderDate());
        existingOrder.setStatus(updatedOrder.getStatus());
        existingOrder.setTotalAmount(updatedOrder.getTotalAmount());
        existingOrder.setShippingAddress(updatedOrder.getShippingAddress());
        existingOrder.setPaymentDetails(updatedOrder.getPaymentDetails());

        // Increment the counter based on the new status
        incrementCounter(updatedOrder.getStatus());

        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        // Decrement the counter based on the order status
        decrementCounter(order.getStatus());

        orderRepository.deleteById(id);
        totalOrdersCounter.increment(-1); // Decrement total orders counter when an order is deleted
    }

    private void processOrder(Order order) {
        switch (order.getStatus()) {
            case PENDING:
                ordersPendingCounter.increment();
                break;
            case PROCESSING:
                ordersProcessingCounter.increment();
                break;
            case SHIPPED:
                ordersShippedCounter.increment();
                break;
            case DELIVERED:
                ordersDeliveredCounter.increment();
                break;
            default:
                // Handle unknown status
                break;
        }
    }

    private void incrementCounter(OrderStatus status) {
        switch (status) {
            case PENDING:
                ordersPendingCounter.increment();
                break;
            case PROCESSING:
                ordersProcessingCounter.increment();
                break;
            case SHIPPED:
                ordersShippedCounter.increment();
                break;
            case DELIVERED:
                ordersDeliveredCounter.increment();
                break;
            default:
                // Handle unknown status
                break;
        }
    }

    private void decrementCounter(OrderStatus status) {
        switch (status) {
            case PENDING:
                ordersPendingCounter.increment(-1);
                break;
            case PROCESSING:
                ordersProcessingCounter.increment(-1);
                break;
            case SHIPPED:
                ordersShippedCounter.increment(-1);
                break;
            case DELIVERED:
                ordersDeliveredCounter.increment(-1);
                break;
            default:
                // Handle unknown status
                break;
        }
    }
}
