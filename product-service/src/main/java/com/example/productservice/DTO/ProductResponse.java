package com.example.productservice.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    private double price;

    private int quantity;

}
