package com.example.purchaseservice.controller;

import com.example.purchaseservice.dto.PurchaseRequest;
import com.example.purchaseservice.dto.PurchaseResponse;
import com.example.purchaseservice.service.PurchaseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping(value = "/buy")
    public ResponseEntity<PurchaseResponse> buy(@RequestBody PurchaseRequest request, HttpServletRequest httpRequest) {
        String username = httpRequest.getHeader("X-User-Id");
        return ResponseEntity.ok(purchaseService.buy(request, username));
    }
}
