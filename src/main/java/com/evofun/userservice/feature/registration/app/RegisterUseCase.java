package com.evofun.userservice.feature.registration.app;

import com.evofun.events.UserRegisteredEvent;
import com.evofun.userservice.shared.domain.repo.UserRepo;
import com.evofun.userservice.shared.domain.model.User;
import com.evofun.userservice.shared.exception.model.FieldErrorDto;
import com.evofun.userservice.infrastructure.outbox.OutboxEvent;
import com.evofun.userservice.infrastructure.outbox.OutboxService;
import com.evofun.userservice.feature.registration.api.request.RegisterRequest;
import com.evofun.userservice.shared.exception.AlreadyExistsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RegisterUseCase {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RegisterUseCase(UserRepo userRepo, PasswordEncoder passwordEncoder, OutboxService outboxService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.outboxService = outboxService;
    }

    @Transactional
    public void register(RegisterRequest request) {
        Optional<User> userNickname = userRepo.findByNickname(request.getNickname());

        List<FieldErrorDto> errors = new ArrayList<>();

        if (userNickname.isPresent()) {
            errors.add(new FieldErrorDto("nickname", "This nickname already exists"));
        }

        Optional<User> userPhoneNumber = userRepo.findByPhoneNumber(request.getPhoneNumber());
        if (userPhoneNumber.isPresent()) {
            errors.add(new FieldErrorDto("phone number", "This phone number already exists"));
        }

        Optional<User> userEmail = userRepo.findByEmail(request.getEmail());
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

        userRepo.save(newUser);

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