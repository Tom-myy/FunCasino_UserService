package com.evofun.userservice.shared.security.jwt.util;

import com.evofun.userservice.shared.domain.model.User;
import com.evofun.userservice.feature.authentication.api.response.AuthResponse;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secrets.user}")
    String userSecret;
    @Value("${security.user.issuer}")
    private String userIssuer;

    public AuthResponse generateUserResponse(User user) {
        return new AuthResponse(generateAuthToken(user), user.getNickname());
    }

    private String generateAuthToken(User user) {
        SecretKey key = new SecretKeySpec(userSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        return Jwts.builder()
                .setIssuer(userIssuer)
                .setSubject(user.getUserId().toString())
                .claim("nickname", user.getNickname())
                .claim("token_type", "USER")
                ///.claim("role", user.getRole()) when add role for users in DB
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) //1 day
                .signWith(key)
                .compact();
    }
}