package com.evofun.userservice.security.jwt;

import com.evofun.userservice.common.db.entity.User;
import com.evofun.userservice.rest.dto.response.JwtResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {
    private final JwtKeysProperties jwtKeysProperties;

    public JwtUtil(JwtKeysProperties jwtKeysProperties) {
        this.jwtKeysProperties = jwtKeysProperties;
    }

    public JwtResponse generateUserResponse(User user) {
        return new JwtResponse(generateAuthToken(user), user.getUserId(), user.getNickname());
    }

    public String generateGameToken(JwtUser user) {
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("nickname", user.getNickname())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 2 * 60 * 1000)) //2 min
                .signWith(jwtKeysProperties.getGameKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateAuthToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("nickname", user.getNickname())
///                .claim("role", user.getRole()) when add role for users in DB
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) //1 day
                .signWith(jwtKeysProperties.getAuthKey(), SignatureAlgorithm.HS256)
                .compact();
    }

///I'll use when create SystemJwtFiler...

///    public String generateSystemToken(String systemName, String role) {
///        return Jwts.builder()
///                .setSubject(systemName)
///                .claim("role", role)
///                .setIssuedAt(new Date())
///                .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
///                .signWith(jwtKeysConfig.getSystemKey(), SignatureAlgorithm.HS256)
///                .compact();
///    }

///    public JwtPayload extractPayload(String token) {
///       Claims claims = extractAllClaimsFromAuthToken(token);
///      return new JwtPayload(
///                UUID.fromString(claims.getSubject()),
///                claims.get("nickname", String.class)
///        );
///    }

///    private Claims extractAllClaimsFromAuthToken(String token) {
///        return Jwts.parserBuilder()
///                .setSigningKey(jwtKeysConfig.getAuthKey())
///                .build()
///                .parseClaimsJws(token)
///                .getBody();
///    }
}

