package com.example.orderservice.Repository;

import com.example.orderservice.Entity.Order;
import com.example.orderservice.Entity.OrderStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatusAndProductId(OrderStatus status, Long productId, Sort s);

    List<Order> findByProductId(Long productId);
}
