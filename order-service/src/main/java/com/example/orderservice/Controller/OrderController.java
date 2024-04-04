package com.example.orderservice.Controller;

import com.example.orderservice.DTO.InventoryItem;
import com.example.orderservice.Entity.Order;
import com.example.orderservice.Entity.OrderStatus;
import com.example.orderservice.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }


    @PostMapping("/create")


    public Order createOrder(@RequestBody Order order)
    {
        order.setOrderDate(LocalDateTime.now());
        return orderService.createOrder(order);
    }

    @PutMapping("update/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {
        return orderService.updateOrder(id, updatedOrder);
    }

    @DeleteMapping("delete/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    @PostMapping("cancel/{id}")//**
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
    }

    @PostMapping("unCancel/{id}")//**
    public void unCancelOrder(@PathVariable Long id) {
        orderService.unCancelOrder(id);
    }

    @PostMapping("updateOrderStatus")
    public Mono<InventoryItem> updateOrderStatus(@RequestBody InventoryItem inventoryItem)
    {
        return Mono.just(orderService.getQuantityAfterUpdatingOrders(inventoryItem));
    }
}






















