package com.evofun.userservice.security.jwt;

import java.util.UUID;

public record JwtPayload(
        UUID userId,
        String nickname
) {}
