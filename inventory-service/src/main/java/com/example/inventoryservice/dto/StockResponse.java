package com.example.inventoryservice.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse implements Serializable {
    private Long id;
    private boolean isInStock;
    
}
