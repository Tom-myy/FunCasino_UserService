package com.evofun.userservice.security.jwt;

import com.evofun.userservice.common.db.entity.User;
import com.evofun.userservice.rest.dto.response.JwtResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    private final JwtKeysConfig jwtKeysConfig;

    public JwtUtil(JwtKeysConfig jwtKeysConfig) {
        this.jwtKeysConfig = jwtKeysConfig;
    }

/*
    //TODO move to file:
    private final Key SECRET_AUTH_KEY = Keys.hmacShaKeyFor("evofun-production-secret-auth-key-2025-abc123$$".getBytes(StandardCharsets.UTF_8));
    private final Key SECRET_GAME_KEY = Keys.hmacShaKeyFor("evofun-production-secret-game-key-2025-abc123$$".getBytes(StandardCharsets.UTF_8));
    private final Key SECRET_SYSTEM_KEY = Keys.hmacShaKeyFor("evofun-production-secret-system-key-2025-abc123$$".getBytes(StandardCharsets.UTF_8));
*/

    public JwtResponse generateUserResponse(User user) {
        return new JwtResponse(generateAuthToken(user), user.getUserID(), user.getNickname());
    }

    public JwtPayload extractPayload(String token) {
        Claims claims = extractAllClaimsFromAuthToken(token);
        return new JwtPayload(
                UUID.fromString(claims.getSubject()),
                claims.get("nickname", String.class)
        );
    }

    private String generateAuthToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUserID().toString())
                .claim("nickname", user.getNickname())
///                .claim("role", user.getRole()) when add role for users in DB
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(jwtKeysConfig.getAuthKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /*    public String generateGameToken(User user) {
            return Jwts.builder()
                    .setSubject(user.getUserID().toString())
                    .claim("nickname", user.getNickname())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 2 * 60 * 1000)) //2 min
                    .signWith(SECRET_GAME_KEY, SignatureAlgorithm.HS256)
                    .compact();
        }*/
    public String generateGameToken(JwtUser user) {
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("nickname", user.getNickname())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 2 * 60 * 1000)) //2 min
                .signWith(jwtKeysConfig.getGameKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateSystemToken(String systemName, String role) {
        return Jwts.builder()
                .setSubject(systemName)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .signWith(jwtKeysConfig.getSystemKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaimsFromAuthToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtKeysConfig.getAuthKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

