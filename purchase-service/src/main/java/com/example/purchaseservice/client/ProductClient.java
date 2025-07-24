package com.example.purchaseservice.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final RestTemplate restTemplate;

    @Value("${services.product.url}")
    private String productServiceUrl;

    public ProductDto getProductById(Long productId) {
        return restTemplate.getForObject(
                productServiceUrl + "/products/" + productId,
                ProductDto.class
        );
    }

    public void decreaseStock(Long productId, int quantity) {
        restTemplate.postForLocation(
                productServiceUrl + "/products/decrease?productId=" + productId + "&amount=" + quantity,
                null
        );
    }

    @Data
    public static class ProductDto {
        private Long id;
        private String productName;
        private int cost;
        private int amountAvailable;
        private Long sellerId;
    }
}
