package com.example.inventoryservice.controllers;

import com.example.inventoryservice.dto.InventoryRequest;
import com.example.inventoryservice.dto.StockResponse;
import com.example.inventoryservice.entities.Inventory;
import com.example.inventoryservice.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    @PostMapping("/create")
    public Inventory create(@RequestBody InventoryRequest inventoryRequest){
        return inventoryService.create(inventoryRequest);
    }
    @GetMapping("/get/{id}")
    public Optional<Inventory> getInventory(@PathVariable Long id){

        return inventoryService.getInventory(id);
    }



    //*************************
    @PutMapping("/update")
    public Inventory updateInventory(@RequestBody Inventory inventory){
        return inventoryService.update(inventory);
    }



    @GetMapping("/all")
    public List<Inventory> getInventories(){
        return inventoryService.getInventories();
    }

    @GetMapping("/stock/{id}")
    public StockResponse isInStock(@PathVariable Long id){
        return inventoryService.isInStock(id);
    }

    @PutMapping("/add")
    public Inventory addQuantity(@RequestBody Inventory inventory){
        return inventoryService.addQuantity(inventory);
    }

    @PutMapping("/retrieve")
    public Inventory retrieveQuantity(@RequestBody Inventory inventory){
        return inventoryService.retrieveQuantity(inventory);
    }

    @PostMapping("/productSufficient")
    public StockResponse productSufficient(@RequestBody Inventory inventory){
        return inventoryService.productSufficient(inventory);
    }

    @PostMapping("/orderSufficient")
    public List<StockResponse> orderSufficient(@RequestBody List<Inventory> inventories){
        return inventoryService.orderSufficient(inventories);
    }

    @PostMapping("/orderRetrieve")
    public List<Inventory> orderRetrieve(@RequestBody List<Inventory> inventories){
        return inventoryService.orderRetrieve(inventories);
    }


    //make all the orders in pending state
    //*************************
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id)
    {
        inventoryService.delete(id);
    }
}
