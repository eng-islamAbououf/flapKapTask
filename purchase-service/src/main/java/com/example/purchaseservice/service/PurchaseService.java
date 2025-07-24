package com.example.purchaseservice.service;

import com.example.purchaseservice.dto.PurchaseRequest;
import com.example.purchaseservice.dto.PurchaseResponse;

public interface PurchaseService {
    PurchaseResponse buy(PurchaseRequest request, String username);
}
