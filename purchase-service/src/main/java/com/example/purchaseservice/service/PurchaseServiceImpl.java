package com.example.purchaseservice.service;

import com.example.purchaseservice.client.ProductClient;
import com.example.purchaseservice.client.UserClient;
import com.example.purchaseservice.dto.PurchaseRequest;
import com.example.purchaseservice.dto.PurchaseResponse;
import com.example.purchaseservice.model.CoinChange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final ProductClient productClient;
    private final UserClient userClient;

    private static final int[] COIN_DENOMINATIONS = {100, 50, 20, 10, 5};

    @Override
    public PurchaseResponse buy(PurchaseRequest request, String username) {
        var product = productClient.getProductById(request.getProductId());
        int totalCost = product.getCost() * request.getQuantity();

        if (request.getQuantity() > product.getAmountAvailable()) {
            throw new ResponseStatusException(BAD_REQUEST, "Insufficient product quantity.");
        }

        int userDeposit = userClient.getUserDeposit(username);

        if (userDeposit < totalCost) {
            throw new ResponseStatusException(BAD_REQUEST, "Insufficient deposit.");
        }

        // Deduct stock and deposit
        productClient.decreaseStock(request.getProductId(), request.getQuantity());
        userClient.deductUserDeposit(username, totalCost);

        // Calculate change
        int remaining = userDeposit - totalCost;
        Map<Integer, Integer> changeMap = calculateChange(remaining);

        return PurchaseResponse.builder()
                .productName(product.getProductName())
                .quantity(request.getQuantity())
                .totalSpent(totalCost)
                .change(CoinChange.builder().coins(changeMap).build())
                .build();
    }

    private Map<Integer, Integer> calculateChange(int remaining) {
        Map<Integer, Integer> change = new LinkedHashMap<>();
        for (int coin : COIN_DENOMINATIONS) {
            int count = remaining / coin;
            if (count > 0) {
                change.put(coin, count);
                remaining %= coin;
            } else {
                change.put(coin, 0);
            }
        }
        return change;
    }
}
