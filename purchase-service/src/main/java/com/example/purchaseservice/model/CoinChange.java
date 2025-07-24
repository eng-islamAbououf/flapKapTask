package com.example.purchaseservice.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CoinChange {
    private Map<Integer, Integer> coins; // e.g., {100=1, 50=0, 20=2}
}
