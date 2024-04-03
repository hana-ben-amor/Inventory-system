package com.example.orderservice.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private Long productId;
    private Long customerId;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private int totalQuantity ;

    private String shippingAddress ;

    private Double totalPrice ;



    public Order(Long orderId, Long productId, Long customerId, LocalDateTime orderDate, OrderStatus status, int totalQuantity, String shippingAddress, Double totalPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.customerId = customerId;
        this.orderDate = LocalDateTime.now();
        this.status = status;
        this.totalQuantity = totalQuantity;
        this.shippingAddress = shippingAddress;
        this.totalPrice = totalPrice;
    }

}

