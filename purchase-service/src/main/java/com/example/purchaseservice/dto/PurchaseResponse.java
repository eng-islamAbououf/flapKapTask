package com.example.purchaseservice.dto;

import com.example.purchaseservice.model.CoinChange;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseResponse {
    private String productName;
    private int quantity;
    private int totalSpent;
    private CoinChange change;
}
