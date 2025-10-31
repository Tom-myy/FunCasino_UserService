package com.evofun.userservice.feature.authentication.app;

import com.evofun.userservice.shared.domain.repo.UserRepo;
import com.evofun.userservice.shared.domain.model.User;
import com.evofun.userservice.feature.authentication.api.response.AuthResponse;
import com.evofun.userservice.feature.authentication.api.request.AuthRequest;
import com.evofun.userservice.feature.authentication.exception.InvalidCredentialsException;
import com.evofun.userservice.shared.security.jwt.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUseCase {
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationUseCase(JwtUtil jwtUtil, UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(AuthRequest request) {
        User user;
        if (request.getLogin().contains("@")) {
            user = userRepo.findByEmail(request.getLogin())
                    .orElseThrow(() -> {
                        return new InvalidCredentialsException("Attempted to authentication with non-existent authentication (email) " + request.getLogin());
                    });
        } else {
            user = userRepo.findByNickname(request.getLogin())
                    .orElseThrow(() -> {
                        return new InvalidCredentialsException("Attempted to authentication with non-existent authentication (nickname) " + request.getLogin());
                    });
        }

        if (!passwordEncoder.matches(request.getPass(), user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect password for nickname " + request.getLogin());
        }

        return jwtUtil.generateUserResponse(user);
    }
}