package com.example.orderservice.Repository;

import com.example.orderservice.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Long> {

}
