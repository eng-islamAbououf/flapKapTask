package com.flapkap.userservice.controller;
import com.flapkap.userservice.dto.request.RoleDTO;
import com.flapkap.userservice.dto.response.RoleResponse;
import com.flapkap.userservice.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/auth/role")
public class RoleController {

    private RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        return roleService.createRole(roleDTO);
    }
}