package com.evofun.userservice.security.config;

import com.evofun.userservice.security.jwt.JwtFilter;
import com.evofun.userservice.security.jwt.JwtKeysConfig;
import com.evofun.userservice.security.jwt.JwtToPrincipalConverter;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtKeysConfig jwtKeysConfig;

    public SecurityConfig(JwtFilter jwtFilter, JwtKeysConfig jwtKeysConfig) {
        this.jwtFilter = jwtFilter;
        this.jwtKeysConfig = jwtKeysConfig;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //TODO WHITE_LIST (like in JwtFiler), but meaningfully
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // ← теперь так
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/internal/**").permitAll()
//                        .requestMatchers("/api/internal/**").hasAuthority("ROLE_SERVICE")
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/ws/**").permitAll()
                                .requestMatchers(
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources/**",
                                        "/webjars/**"
                                ).permitAll()

                                .anyRequest().authenticated()
                )
                //here we use our JwtToPrincipalConverter
                // (for @AuthenticationPrincipal in controllers):
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(new JwtToPrincipalConverter())
                        )
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = jwtKeysConfig.getAuthKey(); // тут уже готовый ключ
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
