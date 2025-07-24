package com.example.productservice.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String productName;
    private int cost;             // in cents
    private int amountAvailable;
    private Long sellerId;        // Set by backend from JWT, not from request
}