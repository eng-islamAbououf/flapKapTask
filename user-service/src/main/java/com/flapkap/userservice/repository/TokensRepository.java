package com.flapkap.userservice.repository;

import com.flapkap.userservice.model.TokenStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokensRepository extends JpaRepository<TokenStore, Long> {
    TokenStore findTokenStoreByUserId(long userId);
    TokenStore findTokenStoreByToken(String token);
}