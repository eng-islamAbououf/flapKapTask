package com.flapkap.userservice.controller;

import com.flapkap.userservice.dto.response.UserResponse;
import com.flapkap.userservice.service.AuthService;
import com.flapkap.userservice.service.CustomUserDetailsService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserDetailsController {

    private final CustomUserDetailsService userService;

//    @GetMapping("/me")
//    public ResponseEntity<UserResponse> getCurrentUser(Principal principal) {
//        return ResponseEntity.ok(userService.getCurrentUser(principal.getName()));
//    }

    @PostMapping("/deposit")
//    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<UserResponse> deposit(@RequestParam @NotNull Integer coin, Principal principal) {
        return ResponseEntity.ok(userService.depositCoin(principal.getName(), coin));
    }

    @PostMapping("/reset")
//    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<UserResponse> resetDeposit(Principal principal) {
        return ResponseEntity.ok(userService.resetUserDeposit(principal.getName()));
    }

}
