package com.flapkap.userservice.mapper;


import com.flapkap.userservice.dto.request.UserDetailsDto;
import com.flapkap.userservice.dto.response.UserResponse;
import com.flapkap.userservice.enums.Profiles;
import com.flapkap.userservice.exception.HelperMessage;
import com.flapkap.userservice.model.Role;
import com.flapkap.userservice.model.User;
import com.flapkap.userservice.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserDetailsMapper {

    private PasswordEncoder bcryptEncoder;
    private RoleRepository roleRepository;

    public void mapTo(UserDetailsDto userDetailsDTO, User user) {

        if (valid(userDetailsDTO.getUsername())) {
            user.setUsername(userDetailsDTO.getUsername());
        }

        if (valid(userDetailsDTO.getRoleId())) {
            Optional<Role> role = roleRepository.findById(Long.parseLong(userDetailsDTO.getRoleId()));
            role.ifPresent(user::setRole);
        }

        if (user.getCreatedDate() == null) {
            user.setCreatedDate(LocalDateTime.now());
        }

        if (valid(userDetailsDTO.getPassword())) {
            user.setPassword(bcryptEncoder.encode(userDetailsDTO.getPassword()));
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HelperMessage.PASSWORD_IMP);

    }

    boolean valid(String obj) {
        return (obj != null && !obj.isEmpty());
    }
    public UserResponse mapTo(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .suspended(user.isSuspended())
                .build();
    }

    @Transactional
    public List<UserResponse> mapTo(List<User> users) {
        List<UserResponse> result = new ArrayList<>();
        for (User user : users) {
            result.add(
                    UserResponse.builder()
                            .userId(user.getId())
                            .username(user.getUsername())
                            .suspended(user.isSuspended())
                            .build()
            );
        }
        return result;
    }



}