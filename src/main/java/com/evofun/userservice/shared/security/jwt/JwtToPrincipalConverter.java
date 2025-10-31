package com.evofun.userservice.shared.security.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.List;
import java.util.UUID;

public class JwtToPrincipalConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        String nickName = jwt.getClaim("nickname");

        JwtUserPrincipal principal = new JwtUserPrincipal(userId, nickName);

        return new UsernamePasswordAuthenticationToken(
                principal,//here we put our JwtUser (or any other
                // class for view (but must implement Principal)

                jwt, //credentials - can put token, pass etc... (if we put token
                // etc. in JwtUser, so we put here null)

                List.of() //now it's empty, but if we need roles in our project,
                // so we'll add them in our JwtUser, and then we'll use them here
                // like authorities (instead od just List.of())
        );
    }
}