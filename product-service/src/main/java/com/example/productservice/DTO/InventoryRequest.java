package com.example.productservice.DTO;

import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequest
{
    private Long id;
    private int quantity;
}
