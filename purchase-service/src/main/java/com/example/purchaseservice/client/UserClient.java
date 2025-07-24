package com.example.purchaseservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;

    @Value("${services.user.url}")
    private String userServiceUrl;

    public int getUserDeposit(String username) {
        String url = userServiceUrl + "/internal/internal/deposit/" + username;
        return restTemplate.getForObject(url, Integer.class);
    }

    public void deductUserDeposit(String username, int amount) {
        String url = userServiceUrl + "/internal/deposit/deduct?username=" + username + "&amount=" + amount;
        restTemplate.postForLocation(url, null);
    }
}
