package com.flapkap.userservice.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDTO {
    @NotBlank
    private String name;
}