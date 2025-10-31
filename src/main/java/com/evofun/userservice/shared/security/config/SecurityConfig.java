package com.evofun.userservice.shared.security.config;

import com.evofun.userservice.shared.security.handler.CustomAccessDeniedHandler;
import com.evofun.userservice.shared.security.jwt.JwtAuthEntryPoint;
import com.evofun.userservice.shared.security.jwt.JwtToPrincipalConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Value("${jwt.secrets.user}")
    String userSecret;

    public SecurityConfig(
            JwtAuthEntryPoint jwtAuthEntryPoint1,
            CustomAccessDeniedHandler customAccessDeniedHandler1) {
        this.jwtAuthEntryPoint = jwtAuthEntryPoint1;
        this.customAccessDeniedHandler = customAccessDeniedHandler1;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                                // === Public ===
                                .requestMatchers(
                                        "/api/v1/user/authentication/**",
                                        "/api/v1/user/register/**",

                                        "/error",

                                        "/actuator/health/**"
                                ).permitAll()

                                // === Swagger ===
                                .requestMatchers(
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-resources/**",
                                        "/webjars/**"
                                ).permitAll()

                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(new JwtToPrincipalConverter())
                        )
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${jwt.secrets.user}") String userSecret,
            @Value("${security.user.issuer}") String userIssuer
    ) {
        SecretKey key = new SecretKeySpec(userSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();

        OAuth2TokenValidator<Jwt> time = new JwtTimestampValidator(Duration.ofSeconds(30));

        OAuth2TokenValidator<Jwt> userValidators = jwt -> {
            if (!userIssuer.equals(jwt.getIssuer() != null ? jwt.getIssuer().toString() : null)) {
                return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "wrong iss for user", null));
            }

            try {
                UUID.fromString(jwt.getSubject());
            } catch (Exception e) {
                return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "sub must be UUID", null));
            }

            return OAuth2TokenValidatorResult.success();
        };

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(time, userValidators));
        return decoder;
    }
}