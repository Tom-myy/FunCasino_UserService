package com.evofun.userservice.rest;

import com.evofun.events.UserRegisteredEvent;
import com.evofun.userservice.common.db.UserService;
import com.evofun.userservice.common.db.entity.User;
import com.evofun.userservice.common.error.FieldErrorDto;
import com.evofun.userservice.kafka.KafkaProducer;
import com.evofun.userservice.outbox.OutboxEvent;
import com.evofun.userservice.outbox.OutboxService;
import com.evofun.userservice.rest.dto.response.JwtResponse;
import com.evofun.userservice.rest.dto.response.LoginRequestDto;
import com.evofun.userservice.rest.dto.response.RegisterRequestDto;
import com.evofun.userservice.rest.exception.AlreadyExistsException;
import com.evofun.userservice.rest.exception.InvalidCredentialsException;
import com.evofun.userservice.security.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducer kafkaProducer;
    private final OutboxService outboxService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public AuthService(JwtUtil jwtUtil, UserService userService, PasswordEncoder passwordEncoder, KafkaProducer kafkaProducer, OutboxService outboxService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.kafkaProducer = kafkaProducer;
        this.outboxService = outboxService;
    }

    public JwtResponse login(LoginRequestDto request) {
        User user;
        if (request.getLogin().contains("@")) {
            user = userService.findByEmail(request.getLogin())
                    .orElseThrow(() -> {
//                        logger.warn("Attempted to login with non-existent login (email): {}", request.getLogin());
                        return new InvalidCredentialsException("Attempted to login with non-existent login (email) " + request.getLogin());
                    });
        } else {
            user = userService.findByNickname(request.getLogin())
                    .orElseThrow(() -> {
//                        logger.warn("Attempted to login with non-existent login (nickname): {}", request.getLogin());
                        return new InvalidCredentialsException("Attempted to login with non-existent login (nickname) " + request.getLogin());
                    });
        }

        if (!passwordEncoder.matches(request.getPass(), user.getPassword())) {
//            logger.warn("Incorrect password for nickname: {}", request.getLogin());
            throw new InvalidCredentialsException("Incorrect password for nickname " + request.getLogin());
        }

        return jwtUtil.generateUserResponse(user);
    }

    @Transactional
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

        UserRegisteredEvent event = new UserRegisteredEvent(newUser.getUserId());

        String aggregateType = newUser.getClass().getSimpleName();
        UUID aggregateId = newUser.getUserId();
        String eventType = event.getClass().getSimpleName();
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        OutboxEvent outboxEvent = new OutboxEvent(aggregateType, aggregateId, eventType, payload);
        outboxService.save(outboxEvent);
    }
}