package com.example.inventoryservice.dto;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest
{
    private Long id;
    private int quantity;
}
