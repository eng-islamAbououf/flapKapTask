package com.flapkap.userservice.controller;

import com.flapkap.userservice.dto.response.UserResponse;
import com.flapkap.userservice.service.CustomUserDetailsService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/internal")
public class InternalUserController {

    private final CustomUserDetailsService userService;

//    @GetMapping("/me")
//    public ResponseEntity<UserResponse> getCurrentUser(Principal principal) {
//        return ResponseEntity.ok(userService.getCurrentUser(principal.getName()));
//    }

    @GetMapping("/internal/deposit/{username}")
    public ResponseEntity<Integer> getUserDeposit(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserDeposit(username));
    }

    @PostMapping("/deposit/deduct")
    public ResponseEntity<Void> deductDeposit(@RequestParam String username, @RequestParam int amount) {
        userService.deductDeposit(username, amount);
        return ResponseEntity.ok().build();
    }

}
