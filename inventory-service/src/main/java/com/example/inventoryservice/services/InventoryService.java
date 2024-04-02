package com.example.inventoryservice.services;

import com.example.inventoryservice.dto.InventoryRequest;
import com.example.inventoryservice.dto.StockResponse;
import com.example.inventoryservice.entities.Inventory;
import com.example.inventoryservice.repositories.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public Inventory create(InventoryRequest inventoryRequest) {
        Inventory inventory =
                Inventory.builder()
                        .id(inventoryRequest.getId())
                        .quantity(inventoryRequest.getQuantity())
                        .build();

        return inventoryRepository.save(inventory);
    }

    public Inventory update(Inventory inventory){
        return inventoryRepository.save(inventory);
    }
    public Optional<Inventory> getInventory(Long id){
        return inventoryRepository.findById(id);
    }

    public List<Inventory> getInventories(){
        return inventoryRepository.findAll();
    }


    public StockResponse isInStock(Long id) {
        Optional<Inventory> inventoryOptional = getInventory(id);

        // Check if the Optional contains a value before accessing it
        if (inventoryOptional.isPresent()) {
            Inventory inventory = inventoryOptional.get();
            boolean isInStock = inventory.getQuantity() > 0;
            return StockResponse.builder()
                    .id(id)
                    .isInStock(isInStock)
                    .build();
        } else {
            // Handle the case where the inventory with the given ID is not found
            throw new RuntimeException("Inventory with ID " + id + " not found");
        }
    }

    public Inventory addQuantity(Inventory inventory){
        Optional<Inventory> inventory1=getInventory(inventory.getId());
        if (inventory1.isPresent()){
            inventory1.get().setQuantity(inventory1.get().getQuantity()+inventory.getQuantity());
            return update(inventory1.get());
        }
        else {
            return null;
        }

    }

    public Inventory retrieveQuantity(Inventory inventory){
        Optional<Inventory> inventory1=getInventory(inventory.getId());
        if (inventory1.isPresent()){
            inventory1.get().setQuantity(inventory1.get().getQuantity()-inventory.getQuantity());
            return update(inventory1.get());
        }
        else {
            return null;
        }

    }

    public StockResponse productSufficient(Inventory inventory){
        Optional<Inventory> inventory1=getInventory(inventory.getId());
        if (inventory1.isPresent()){
        return StockResponse.builder().id(inventory.getId()).isInStock(inventory1.get().getQuantity()>=inventory.getQuantity()).build();}
        else{
            return null;
            }
    }

    public List<StockResponse> orderSufficient(List<Inventory> inventories){
        return inventories.stream().map(this::productSufficient).collect(Collectors.toList());
    }

    public List<Inventory> orderRetrieve(List<Inventory> inventories){
        return inventories.stream().map(this::retrieveQuantity).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        inventoryRepository.deleteById(id);
    }
}
