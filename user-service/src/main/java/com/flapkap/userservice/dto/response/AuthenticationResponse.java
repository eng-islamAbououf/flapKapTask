package com.flapkap.userservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationResponse {

    private String token;

    public AuthenticationResponse() {

    }

    public AuthenticationResponse(String token) {
        super();
        this.token = token;
    }


}