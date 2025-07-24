package com.flapkap.userservice.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flapkap.userservice.model.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import jakarta.validation.constraints.Email;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsDto {

    private String username;
    private String password;
    private String roleId;

}
