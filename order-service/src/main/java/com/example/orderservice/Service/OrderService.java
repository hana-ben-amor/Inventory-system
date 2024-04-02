package com.example.orderservice.Service;

import com.example.orderservice.DTO.InventoryItem;
import com.example.orderservice.Entity.Order;
import com.example.orderservice.Entity.OrderStatus;
import com.example.orderservice.Repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    private final RestTemplate restTemplate = new RestTemplate() ;
    private final String inventoryPath = "http://localhost:8084/inventories";



    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Transactional
    public Order createOrder(Order order) {

        Long poductId= order.getProductId();

        ResponseEntity<InventoryItem> inventoryResponce
                = restTemplate.getForEntity(inventoryPath +"/get/" + poductId, InventoryItem.class);

        if(!inventoryResponce.getStatusCode().is2xxSuccessful())
        {
            throw new RuntimeException("Failed to retrieve inventory. Status code: " + inventoryResponce.getStatusCodeValue());
        }


        InventoryItem inventoryItem = inventoryResponce.getBody();
        if (inventoryItem == null)
        {
            throw new RuntimeException("Failed to retrieve inventory. inventoryItem is null");
        }


        if(inventoryItem.getQuantity() < order.getTotalQuantity())
        {
            order.setStatus(OrderStatus.PENDING);//pending is waiting for the inventory to be refilled !
            System.out.println("The Quantity in Inventory is less the the Quantity demanded in the order " + order.getOrderId() + " so its initial status will be PENDING");
            // throw new RuntimeException("The Quantity in Inventory is less the the Quantity demanded in the order " + order.getOrderId());
        }else {
            int remainingQuantity = inventoryItem.getQuantity() - order.getTotalQuantity();
            inventoryItem.setQuantity(remainingQuantity);
            restTemplate.put(inventoryPath +"/update",inventoryItem,InventoryItem.class);

            order.setStatus(OrderStatus.PROCESSING);


        }
        return orderRepository.save(order);
    }

    //use save here instead of all this code
    public Order updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        existingOrder.setCustomerId(updatedOrder.getCustomerId());
        existingOrder.setOrderDate(updatedOrder.getOrderDate());
        existingOrder.setStatus(updatedOrder.getStatus());
        existingOrder.setTotalQuantity(updatedOrder.getTotalQuantity());
        existingOrder.setShippingAddress(updatedOrder.getShippingAddress());
        existingOrder.setTotalPrice(updatedOrder.getTotalPrice());

        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Optional<Order> order_o = orderRepository.findById(id);
        Order order = order_o.get();//TODO:: check if null
        order.setStatus(OrderStatus.CANCELED);


        ResponseEntity<InventoryItem> inventoryResponce
                = restTemplate.getForEntity(inventoryPath +"/get/" + order.getProductId(), InventoryItem.class);

        if(!inventoryResponce.getStatusCode().is2xxSuccessful())
        {
            throw new RuntimeException("Failed to retrieve inventory. Status code: " + inventoryResponce.getStatusCodeValue());
        }

        InventoryItem inventoryItem = inventoryResponce.getBody();
        if (inventoryItem == null)
        {
            throw new RuntimeException("Failed to retrieve inventory. inventoryItem is null");
        }

        inventoryItem.setQuantity(inventoryItem.getQuantity() + order.getTotalQuantity()); //return the quantity of the order to the inventory
        restTemplate.put(inventoryPath +"/update",inventoryItem,InventoryItem.class);

        orderRepository.save(order);
    }

    public void unCancelOrder(Long id) {

        Optional<Order> order_o = orderRepository.findById(id);
        Order order = order_o.get();//TODO:: check if null
        if (order.getStatus() != OrderStatus.CANCELED) {
            throw new RuntimeException("Failed Action : Uncancelling a " + order.getStatus() + " order is not Allowed !");
        }

        // to uncancel the order + check if the inventory is suffisiant and all other constraints
        // + it will not create a new one because the order here have its id so it will just update it in the database
        createOrder(order);
    }

    //unpending function
}

