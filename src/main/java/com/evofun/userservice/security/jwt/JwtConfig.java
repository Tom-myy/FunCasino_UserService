package com.evofun.userservice.security.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtKeysProperties.class)
public class JwtConfig {
}
