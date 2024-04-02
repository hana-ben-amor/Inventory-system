package com.example.orderservice.DTO;


import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {

    @Id //maybe remove the Id?
    private Long id;
    private int quantity;
}
