package com.evofun.userservice.security.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtKeysConfig {

    @Value("${jwt.auth-secret}")
    private String authSecret;

    @Value("${jwt.game-secret}")
    private String gameSecret;

    @Value("${jwt.system-secret}")
    private String systemSecret;

    public SecretKey getAuthKey() {
        return Keys.hmacShaKeyFor(authSecret.getBytes(StandardCharsets.UTF_8));
    }

    public SecretKey getGameKey() {
        return Keys.hmacShaKeyFor(gameSecret.getBytes(StandardCharsets.UTF_8));
    }

    public SecretKey getSystemKey() {
        return Keys.hmacShaKeyFor(systemSecret.getBytes(StandardCharsets.UTF_8));
    }
}
