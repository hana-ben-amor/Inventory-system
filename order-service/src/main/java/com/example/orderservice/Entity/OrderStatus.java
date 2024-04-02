package com.example.orderservice.Entity;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELED
}

//TDOD:: add the 'RETURNED" order status --> in case the costumer doesn't like the products