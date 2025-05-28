package com.evofun.userservice.security.jwt;

import com.evofun.userservice.common.error.ErrorCode;
import com.evofun.userservice.common.error.ErrorDto;
import com.evofun.userservice.common.error.ExceptionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private static final List<String> WHITELIST = List.of(//TODO think over
            "/api/internal/**",

            "/ws/**",//TODO mb remove from here and do smth like game-token

            "/api/auth/**",

            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/swagger-resources/**",
            "/webjars/**"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        //TODO think over
        String path = request.getRequestURI();
        AntPathMatcher matcher = new AntPathMatcher();
        return WHITELIST.stream().anyMatch(pattern -> matcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

/*        // 👇 если это WebSocket-запрос — пропускаем без JWT-проверки
        if (path.startsWith("/ws") || path.startsWith("/websocket")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ❗ Пропускаем открытые пути (например, регистрация и логин)
        if (path.startsWith("/api/auth/register")
                || path.startsWith("/api/auth/login")
                || path.startsWith("/api/internal/updateUsersAfterGame")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.equals("/swagger-ui.html")) {
            logger.debug(">>> Swagger path — skipping JwtFilter");
            filterChain.doFilter(request, response);
            return;
        }*/



        String header = request.getHeader("Authorization");

        // Проверка: есть ли заголовок и начинается ли он с "Bearer "
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            JwtPayload payload;

            try {
                payload = jwtUtil.extractPayload(token);
            } catch (ExpiredJwtException expired) {
                logger.warn("Expired JWT token: {}", expired);
                SecurityContextHolder.clearContext();
                handleErrorResponse(response, "Expired JWT token.");
                return;
            } catch (JwtException e) {
                logger.warn("Invalid JWT token: {}", e);
                SecurityContextHolder.clearContext();
                handleErrorResponse(response, "Invalid JWT token.");
                return;
            }

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(payload.userId(), null, null);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

        } else {
            handleErrorResponse(response, "Missing or malformed Authorization header.");
            return;

        }

        filterChain.doFilter(request, response);
    }

    public void handleErrorResponse(HttpServletResponse response, String reasonPrefix) throws IOException {
        String code = ExceptionUtils.generateErrorId("VAL");
        String message = reasonPrefix + " ERROR-CODE: " + code;

        ErrorDto errorDto = new ErrorDto(ErrorCode.AUTHORIZATION, code, message, null);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        String json = new ObjectMapper().writeValueAsString(errorDto);
        response.getWriter().write(json);

        response.flushBuffer();
    }
}
