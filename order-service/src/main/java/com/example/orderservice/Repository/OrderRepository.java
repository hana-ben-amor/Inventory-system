package com.example.orderservice.Repository;

import com.example.orderservice.Entity.Order;
import com.example.orderservice.Entity.OrderStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatusAndProductId(OrderStatus status, Long productId, Sort s);

}
