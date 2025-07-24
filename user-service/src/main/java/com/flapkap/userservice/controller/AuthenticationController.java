package com.flapkap.userservice.controller;

import com.flapkap.userservice.dto.request.LoginRequest;
import com.flapkap.userservice.dto.request.UserDetailsDto;
import com.flapkap.userservice.dto.response.AuthenticationResponse;
import com.flapkap.userservice.security.JwtUtil;
import com.flapkap.userservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private AuthService customUserDetailsService;


    @PostMapping(value = "/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody LoginRequest loginRequest) {

        return customUserDetailsService.login(loginRequest);
    }

    @PostMapping(value = "/register")
    public AuthenticationResponse register(@Valid @RequestBody UserDetailsDto userDetailsDto) {
        return customUserDetailsService.createUser(userDetailsDto);

    }

}