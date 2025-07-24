package com.flapkap.userservice.service;

import com.flapkap.userservice.dto.request.LoginRequest;
import com.flapkap.userservice.dto.request.UserDetailsDto;
import com.flapkap.userservice.dto.response.AuthenticationResponse;
import com.flapkap.userservice.exception.HelperMessage;
import com.flapkap.userservice.mapper.UserDetailsMapper;
import com.flapkap.userservice.model.TokenStore;
import com.flapkap.userservice.model.User;
import com.flapkap.userservice.repository.TokensRepository;
import com.flapkap.userservice.repository.UserRepository;
import com.flapkap.userservice.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService implements UserDetailsService {

    TokensRepository tokensRepository;
    private UserRepository userRepository;
    private UserDetailsMapper userDetailsMapper;
    private PasswordEncoder passwordEncoder;
    JwtUtil jwtUtil;



    public AuthenticationResponse createUser(UserDetailsDto userDetailsDTO) {

        // Map DTO to User entity early to get phone/email values
        User newUser = new User();
        userDetailsMapper.mapTo(userDetailsDTO, newUser);

        Optional<User> existingUserByUsername;
        if (userDetailsDTO.getUsername() != null && !userDetailsDTO.getUsername().isBlank()) {
            existingUserByUsername = userRepository.findByUsername(userDetailsDTO.getUsername());
            if (existingUserByUsername.isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, HelperMessage.USER_EXIST);
            }
        }

        User user =  userRepository.save(newUser);
        return new AuthenticationResponse(jwtUtil.generateToken(user));
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {


        Optional<User> existingUserByUsername;
        if (loginRequest.getUsername() != null && !loginRequest.getUsername().isBlank()) {
            existingUserByUsername = userRepository.findByUsername(loginRequest.getUsername());

            if (existingUserByUsername.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HelperMessage.USER_NOT_FOUND);
            }
            User user = existingUserByUsername.get();
            if(passwordEncoder.matches(loginRequest.getPassword() , user.getPassword())){
                return new AuthenticationResponse(jwtUtil.generateToken(user));
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HelperMessage.USER_NOT_FOUND);

        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HelperMessage.USER_NOT_FOUND);
    }

    public void logout() {
        User user = jwtUtil.getUserDataFromToken();
        TokenStore token = tokensRepository.findTokenStoreByUserId(user.getId());
        if (token != null){
            tokensRepository.delete(token);
        }else
            throw new  ResponseStatusException(HttpStatus.BAD_REQUEST, HelperMessage.ALREADY_LOGOUT);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Step 1: Find the user by email in your database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    // This is the CRUCIAL part: Throw UsernameNotFoundException if user not found
                    System.err.println("### ERROR: User not found with username: " + username); // Temp debug print
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        // Step 2: If the user is found, convert your custom User object to Spring Security's UserDetails
        // You'll need to adapt this based on how your User and Profile/Role entities are structured
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName()));


        // Return a Spring Security UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),       // Username (username in your case)
                user.getPassword(),    // Hashed password from your database
                authorities            // User's roles/authorities
        );
    }
}