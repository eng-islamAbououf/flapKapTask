package com.flapkap.userservice.service;

import com.flapkap.userservice.dto.response.UserResponse;
import com.flapkap.userservice.exception.HelperMessage;
import com.flapkap.userservice.model.Role;
import com.flapkap.userservice.model.User;
import com.flapkap.userservice.repository.RoleRepository;
import com.flapkap.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {

    private final UserRepository userRepository;

    private static final List<Integer> ALLOWED_COINS = Arrays.asList(5, 10, 20, 50, 100);

    public UserResponse getCurrentUser(String username) {
        Optional<User> temp = userRepository.findByUsername(username);
        if(temp.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HelperMessage.USER_NOT_FOUND);
        User user = temp.get();
        return UserResponse.from(user);
    }

    public UserResponse depositCoin(String username, int coin) {

        User user = getUser(username);

        if(!user.getRole().getName().equalsIgnoreCase("BUYER")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unauthorized");
        }

        if (!ALLOWED_COINS.contains(coin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid coin denomination");
        }

        user.setDeposit(user.getDeposit() + coin);
        userRepository.save(user);
        return UserResponse.from(user);
    }
    public UserResponse resetUserDeposit(String username) {

        User user = getUser(username);
        user.setDeposit(0);
        userRepository.save(user);
        return UserResponse.from(user);
    }

    private User getUser(String username){

        Optional<User> temp = userRepository.findByUsername(username);
        if(temp.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HelperMessage.USER_NOT_FOUND);
        return temp.get();
    }
    public Integer getUserDeposit(String username) {
        User user = getUser(username);
        return user.getDeposit();
    }

    public void deductDeposit(String username, int amount) {
        // Fetch the user by username
        User user = getUser(username);

        // Validate the user is a BUYER
        if (!user.getRole().getName().equalsIgnoreCase("BUYER")) {
            throw new AccessDeniedException("Only BUYER users can have a deposit");
        }

        // Validate sufficient balance
        int currentDeposit = user.getDeposit();
        if (currentDeposit < amount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient deposit. Available: " + currentDeposit + ", required: " + amount);
        }

        // Deduct the amount
        user.setDeposit(currentDeposit - amount);

        // Save changes
        userRepository.save(user);
    }

}
