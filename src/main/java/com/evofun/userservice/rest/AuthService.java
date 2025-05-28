package com.evofun.userservice.rest;

import com.evofun.userservice.common.db.UserService;
import com.evofun.userservice.common.db.entity.User;
import com.evofun.userservice.common.error.FieldErrorDto;
import com.evofun.userservice.rest.dto.response.JwtResponse;
import com.evofun.userservice.rest.dto.response.LoginRequestDto;
import com.evofun.userservice.rest.dto.response.RegisterRequestDto;
import com.evofun.userservice.rest.exception.AlreadyExistsException;
import com.evofun.userservice.rest.exception.InvalidCredentialsException;
import com.evofun.userservice.security.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtUtil jwtUtil, UserService userService, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public JwtResponse login(LoginRequestDto request) {
        User user;
        if (request.getLogin().contains("@")) {
            user = userService.findByEmail(request.getLogin())
                    .orElseThrow(() -> {
                        logger.warn("Attempted to login with non-existent login (email): {}", request.getLogin());
                        return new InvalidCredentialsException();
                    });
        } else {
            user = userService.findByNickname(request.getLogin())
                    .orElseThrow(() -> {
                        logger.warn("Attempted to login with non-existent login (nickname): {}", request.getLogin());
                        return new InvalidCredentialsException();
                    });
        }

        if (!passwordEncoder.matches(request.getPass(), user.getPass())) {
            logger.warn("Incorrect password for nickname: {}", request.getLogin());
            throw new InvalidCredentialsException();
        }

        return jwtUtil.generateUserResponse(user);
    }

    public void register(RegisterRequestDto request) {
        Optional<User> userNickname = userService.findByNickname(request.getNickname());

        List<FieldErrorDto> errors = new ArrayList<>();

        if (userNickname.isPresent()) {
            errors.add(new FieldErrorDto("nickname", "This nickname already exists"));
        }

        Optional<User> userPhoneNumber = userService.findByPhoneNumber(request.getPhoneNumber());
        if (userPhoneNumber.isPresent()) {
            errors.add(new FieldErrorDto("phone number", "This phone number already exists"));
        }

        Optional<User> userEmail = userService.findByEmail(request.getEmail());
        if (userEmail.isPresent()) {
            errors.add(new FieldErrorDto("email", "This email already exists"));
        }

        if (!errors.isEmpty()) {
            throw new AlreadyExistsException(errors);
        }

        User newUser = new User(
                request.getName(),
                request.getSurname(),
                request.getNickname(),
                request.getPhoneNumber(),
                request.getEmail(),
                passwordEncoder.encode(request.getPass())
                );

        userService.saveUser(newUser);
    }
}