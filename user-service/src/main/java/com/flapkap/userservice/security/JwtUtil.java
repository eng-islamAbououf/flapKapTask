package com.flapkap.userservice.security;

import com.flapkap.userservice.exception.HelperMessage;
import com.flapkap.userservice.model.TokenStore;
import com.flapkap.userservice.model.User;
import com.flapkap.userservice.repository.TokensRepository;
import com.flapkap.userservice.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException; // Ensure this specific import is there for SignatureException
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JwtUtil {

    private String secret;
    private int jwtExpirationInMs;
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private TokensRepository tokensRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Value("${jwt.expiration}")
    public void setJwtExpirationInMs(int jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = new ArrayList<>();
        if (user.getRole() != null) {
            roles.add(user.getRole().getName());
        }

        claims.put("roles", roles);
        String token = doGenerateToken(claims, user.getUsername());
        storeTokenInStore(user, token);
        return token;
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public Map<String, Object> getMapFromIoJsonwebtokenClaims(Claims claims) {
        return new HashMap<>(claims);
    }

    // --- UPDATED validateToken with detailed logging ---
    public boolean validateToken(String authToken, UserDetails userDetails) {
        // --- NEW System.out.println calls ---

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken)
                    .getPayload();

            final String usernameFromToken = claims.getSubject();


            boolean usernameMatches = usernameFromToken.equals(userDetails.getUsername());
            boolean tokenNotExpired = !isTokenExpired(claims);



            // --- Log the final return value ---
            boolean finalResult = usernameMatches && tokenNotExpired;
            return finalResult;

        } catch (SignatureException ex) {
            return false;
        } catch (MalformedJwtException ex) {
            return false;
        } catch (ExpiredJwtException ex) {
            return false;
        } catch (UnsupportedJwtException ex) {
            return false;
        } catch (IllegalArgumentException ex) {
            return false;
        } catch (Exception ex) {
            ex.printStackTrace(); // Print full stack trace for unexpected errors
            return false;
        }
    }

    // Helper method to check token expiration based on Claims object
    private Boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public User getUserDataFromToken() {
        String token = getToken();
        String actualToken = extractBearerFromToken(token);
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(actualToken)
                .getPayload();
        Optional<User> user = userRepository.findByUsername(claims.getSubject());
        if (user.isPresent())
            return user.get();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HelperMessage.USER_NOT_FOUND);
    }
    public User getUserDataFromToken(String token) {
        String actualToken = extractBearerFromToken(token);
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(actualToken)
                .getPayload();
        Optional<User> user = userRepository.findByUsername(claims.getSubject());
        if (user.isPresent())
            return user.get();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HelperMessage.USER_NOT_FOUND);
    }


    public List<SimpleGrantedAuthority> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        List<?> rolesInClaimRaw = claims.get("roles", List.class);
        if (rolesInClaimRaw != null) {
            for (Object role : rolesInClaimRaw) {
                if (role instanceof String) {
                    roles.add(new SimpleGrantedAuthority("ROLE_"+ role));
                }
            }
        }
        return roles;
    }

    private String extractBearerFromToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    private void storeTokenInStore(User user, String token) {
        TokenStore t = tokensRepository.findTokenStoreByUserId(user.getId());
        if (t == null) {
            t = TokenStore.builder()
                    .token(token)
                    .notUsed(false)
                    .userId(user.getId())
                    .createdAt(new Date(System.currentTimeMillis()))
                    .expiredDate(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                    .build();
        }else{
            t.setCreatedAt(new Date(System.currentTimeMillis()));
            t.setExpiredDate(new Date(System.currentTimeMillis() + jwtExpirationInMs));
            t.setToken(token);
            t.setNotUsed(false);
        }
        tokensRepository.save(t);
    }

    public static String getToken() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getRequest()
                .getHeader(AUTHORIZATION_HEADER)
                .substring(7);
    }
}