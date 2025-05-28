package com.evofun.userservice.security.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.List;
import java.util.UUID;

public class JwtToPrincipalConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        //here we add some fields which we wanna see at JwtUser,
        //then we'll use them in controllers etc...
        UUID userId = UUID.fromString(jwt.getSubject());
        String nickName = jwt.getClaim("nickname");

        JwtUser principal = new JwtUser(userId, nickName); //We'll use it in @AuthenticationPrincipal

///        List<SimpleGrantedAuthority> authorities = rolesFromJwt.stream()
///                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
///                .toList();

        return new UsernamePasswordAuthenticationToken(
                principal,//here we put our JwtUser (or any other
                // class for view (but must implement Principal)

                jwt, //credentials - can put token, pass etc... (if we put token
                // etc. in JwtUser, so we put here null)

///                authorities
                List.of()//now it's empty, but if we need roles in our project,
                // so we'll add them in our JwtUser, and then we'll use them here
                // like authorities (instead od just List.of())
        );
    }
}
