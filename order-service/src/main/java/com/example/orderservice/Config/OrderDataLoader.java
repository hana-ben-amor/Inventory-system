package com.example.orderservice.Config;

import com.example.orderservice.Entity.Order;
import com.example.orderservice.Entity.OrderStatus;
import com.example.orderservice.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderDataLoader implements CommandLineRunner {

    @Autowired
    private OrderService orderService;

    @Override
    public void run(String... args) throws Exception {
        // Uncomment this section to add 5 sample orders at the start of the application for testing purposes
//        for (int i = 1; i <= 5; i++) {
//            Order order = new Order();
//            order.setCustomerId((long) i); // Assuming customer IDs start from 1
//            order.setOrderDate(LocalDateTime.now());
//            order.setStatus(OrderStatus.PENDING);
//            order.setTotalAmount(100 * i); // Assuming each order has a total amount of 100 units
//            order.setShippingAddress("Address " + i);
//            order.setPaymentDetails("Payment details " + i);
//            orderService.createOrder(order);
//        }
    }
}
