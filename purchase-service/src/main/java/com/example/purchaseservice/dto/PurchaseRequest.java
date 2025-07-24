package com.example.purchaseservice.dto;

import lombok.Data;

@Data
public class PurchaseRequest {
    private Long productId;
    private int quantity;
}
