package com.flapkap.userservice.security;

import com.flapkap.userservice.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException; // Added for more specific catch
import io.jsonwebtoken.SignatureException;   // Added for more specific catch
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException; // Keep if JwtUtil might throw it
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
@Slf4j // Lombok annotation for logging
public class CustomJwtAuthenticationFilter extends OncePerRequestFilter {

    // Remove this field, JwtUtil manages the secret internally now
    // @Value("${jwt.secret}")
    // private String secret;

    @Lazy
    private JwtUtil jwtTokenUtil;


    // IMPORTANT: Autowire your CustomUserDetailsService to load user details
    private AuthService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String jwtToken = extractJwtFromRequest(request);
        String username ;

        // Only proceed if a JWT token is present in the request
        if (StringUtils.hasText(jwtToken)) {
            try {
                // 1. Extract username from the JWT token.
                // Your JwtUtil.getUsernameFromToken method will now internally handle parsing
                // and throw exceptions (ExpiredJwtException, MalformedJwtException, etc.)
                // if the token is invalid or expired.
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);

                // 2. If a username was successfully extracted and there's no existing authentication in the context
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 3. Load UserDetails for the extracted username
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                    // 4. Validate the token fully (username match and non-expired).
                    // Your JwtUtil.validateToken now returns a boolean and handles its own exceptions internally.
                    if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                        // If the token is valid for the loaded user, create an authentication object
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        // 5. Set the authentication in Spring Security's context
                        // This indicates to Spring Security that the current user is authenticated
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // Optional: Set user details in your custom WebTokenDetails utility

                    } else {
                        // Log if the token is present and username extracted, but full validation failed
                        log.warn("JWT Token validation failed for user: {} (token possibly invalid or details mismatch)", username);
                    }
                }
            } catch (ExpiredJwtException ex) {
                // Catch specific exceptions for clearer logging and handling
                log.warn("JWT Token has expired: {}", ex.getMessage());
                request.setAttribute("exception", ex); // Set attribute for Spring's exception handler (if configured)
            } catch (SignatureException | MalformedJwtException | IllegalArgumentException ex) {
                log.warn("Invalid JWT Token (signature, format, or illegal argument): {}", ex.getMessage());
                request.setAttribute("exception", ex);
            } catch (BadCredentialsException ex) {
                // Catch this if your UserDetailsService or other parts throw it
                log.warn("Bad credentials during JWT authentication: {}", ex.getMessage());
                request.setAttribute("exception", ex);
            } catch (Exception ex) { // Catch any other unexpected exceptions during the process
                log.error("An unexpected error occurred during JWT authentication: {}", ex.getMessage(), ex);
                request.setAttribute("exception", ex);
            }
        } else {
            // Log if no JWT token is found (common for requests not requiring auth or initial requests)
            log.debug("No JWT Token found in request or token is empty/malformed for request to {}", request.getRequestURI());
        }

        // Continue the filter chain to the next filter or controller
        chain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header.
     * Assumes "Bearer " prefix.
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Return the token string after "Bearer "
        }
        return null; // No Bearer token found
    }

    // This method is no longer needed because the username is directly obtained and used.
    // private void setUserTokenDetails(Claims claims) {
    //    String username = claims.get("sub", String.class);
    //    webTokenDetails.setUsername(username);
    // }
}