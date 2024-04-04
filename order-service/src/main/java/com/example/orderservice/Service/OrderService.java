package com.example.orderservice.Service;

import com.example.orderservice.DTO.InventoryItem;
import com.example.orderservice.Entity.Order;
import com.example.orderservice.Entity.OrderStatus;
import com.example.orderservice.Repository.OrderRepository;
import com.netflix.discovery.converters.Auto;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;


    private final KafkaTemplate<String,String> kafkaTemplate;
    private  Counter ordersPendingCounter;
    private  Counter ordersProcessingCounter;
    private  Counter ordersShippedCounter;
    private  Counter ordersDeliveredCounter;
    private  Counter totalOrdersCounter;



    public OrderService(KafkaTemplate<String, String> kafkaTemplate, MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
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
    public Order createOrder(Order order , boolean isUpdating) {


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
        processOrder(order);
        if(!isUpdating) {
            totalOrdersCounter.increment();
        }
        return orderRepository.save(order);
    }

    //use save here instead of all this code
    public Order updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        // Decrement the counter based on the existing status
        decrementCounter(existingOrder.getStatus());

        // Update the order
        existingOrder.setCustomerId(updatedOrder.getCustomerId());
        existingOrder.setOrderDate(updatedOrder.getOrderDate());
        existingOrder.setStatus(updatedOrder.getStatus());
        // Increment the counter based on the new status
        incrementCounter(updatedOrder.getStatus());
        existingOrder.setTotalQuantity(updatedOrder.getTotalQuantity());
        existingOrder.setShippingAddress(updatedOrder.getShippingAddress());
        existingOrder.setTotalPrice(updatedOrder.getTotalPrice());

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
        createOrder(order,true);
    }

    public InventoryItem getQuantityAfterUpdatingOrders(InventoryItem inventoryItem) {
        Sort sort = Sort.by(Sort.Direction.ASC, "orderDate");
        List<Order> orders = orderRepository
                .findByStatusAndProductId(OrderStatus.PENDING,inventoryItem.getId(),sort);

        System.out.println(orders);
        for(Order o : orders)
        {
            System.out.println(o);
        }
        System.out.println("updating is called");
        orders.stream().map(
                (e)->{
                    if(inventoryItem.getQuantity()>=e.getTotalQuantity())
                    {
                        System.out.println("entered for order : " + e.getOrderId());
                        e.setStatus(OrderStatus.PROCESSING);
                        inventoryItem.setQuantity(inventoryItem.getQuantity()- e.getTotalQuantity());
                        return orderRepository.save(e);
                    }else {
                        System.out.println(inventoryItem.getQuantity() >= e.getTotalQuantity());
                    }
                    return 0;
                }
        ).collect(Collectors.toList());;


        System.out.println(inventoryItem);
        return inventoryItem;
    }

    //unpending function



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






    @KafkaListener(topics = "requestTopic")
    public void checkProductExist(String productId)
    {
        List<Order> orders
                = orderRepository.findByProductId(Long.valueOf(productId));

        kafkaTemplate.send("responseTopic",orders.isEmpty()+""+productId.toString());


    }


}
