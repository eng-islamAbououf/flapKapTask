package com.flapkap.userservice.dto.response;

import com.flapkap.userservice.dto.request.RoleDTO;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private long id;
    private String name;
    private List<RoleDTO> roles;
}