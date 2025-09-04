package com.evofun.userservice.security.config;

import com.evofun.userservice.security.CustomAccessDeniedHandler;
import com.evofun.userservice.security.JwtAuthEntryPoint;
import com.evofun.userservice.security.jwt.JwtKeysProperties;
import com.evofun.userservice.security.jwt.JwtToPrincipalConverter;
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
import javax.crypto.SecretKey;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
///    private final SystemJwtFilter systemJwtFilter;
    private final JwtKeysProperties jwtKeysProperties;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(/*JwtFilter jwtFilter,*/ JwtKeysProperties jwtKeysProperties,
                                                   JwtAuthEntryPoint jwtAuthEntryPoint1,
                                                   CustomAccessDeniedHandler customAccessDeniedHandler1) {
///        this.jwtFilter = jwtFilter; //add here future SystemJwtFilter
        this.jwtKeysProperties = jwtKeysProperties;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint1;
        this.customAccessDeniedHandler = customAccessDeniedHandler1;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //TODO WHITE_LIST (like in JwtFiler), but meaningfully
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // ← теперь так
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/internal/**").permitAll()
                                .requestMatchers("/error").permitAll()
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
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(new JwtToPrincipalConverter())
                        )
                )
///                .addFilterBefore(systemJwtFilter, UsernamePasswordAuthenticationFilter.class) //for future SystemJwtFilter
                //TODO when create SystemJwtFiler - add it here
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = jwtKeysProperties.getAuthKey();
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
