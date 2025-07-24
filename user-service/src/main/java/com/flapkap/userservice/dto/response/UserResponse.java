package com.flapkap.userservice.dto.response;

import com.flapkap.userservice.model.User;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse  implements Serializable {
    private long userId;
    private String username;
    private boolean suspended = false;

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.isSuspended());
    }
}