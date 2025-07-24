package com.flapkap.userservice.service;

import com.flapkap.userservice.dto.request.RoleDTO;
import com.flapkap.userservice.dto.response.RoleResponse;
import com.flapkap.userservice.exception.HelperMessage;
import com.flapkap.userservice.model.Role;
import com.flapkap.userservice.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class RoleService {

    private RoleRepository roleRepository;

    public ResponseEntity<RoleResponse> createRole(RoleDTO roleDTO) {
        Role role1 = roleRepository.getRoleByName(roleDTO.getName());
        if (role1 == null) {
            Role role = Role.builder()
                    .name(roleDTO.getName())
                    .build();
            Role savedRole = roleRepository.save(role);
            return ResponseEntity.ok(RoleResponse.builder()
                    .id(savedRole.getId())
                    .name(savedRole.getName())
                    .build());
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HelperMessage.ROLE_EXIST);
    }

    public List<Role> findRoles(List<Long> roleIds) {
        return roleRepository.findAllByIdIn(roleIds);
    }
}