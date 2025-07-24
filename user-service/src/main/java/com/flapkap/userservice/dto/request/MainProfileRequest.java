package com.flapkap.userservice.dto.request;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainProfileRequest {
    @NotBlank
    private String name;
    @NotNull
    private List<Long> rolesIds;
}