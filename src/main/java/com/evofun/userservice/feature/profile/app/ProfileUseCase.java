package com.evofun.userservice.feature.profile.app;

import com.evofun.userservice.feature.profile.app.mapper.UserMapper;
import com.evofun.userservice.shared.domain.model.User;
import com.evofun.userservice.shared.domain.repo.UserRepo;
import com.evofun.userservice.feature.profile.api.request.UpdateProfileRequest;
import com.evofun.userservice.feature.profile.api.response.ProfileResponse;
import com.evofun.userservice.shared.exception.AlreadyExistsException;
import com.evofun.userservice.shared.exception.model.FieldErrorDto;
import com.evofun.userservice.feature.profile.exception.UserNotFoundException;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProfileUseCase {
    private final UserRepo userRepo;
    private final UserMapper userMapper;

    public ProfileUseCase(UserRepo userRepo, UserMapper userMapper) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;
    }

    public ProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(
                "User with UUID (" + userId + ") not found in DB during profile updating (REST).",
                "User not found. Sign up or contact support."
        ));

        boolean isSmthChanged = applyAndCheckChanges(request, user);

        if (isSmthChanged)
            return userMapper.toProfile(userRepo.save(user));

        return userMapper.toProfile(user);
    }

    public ProfileResponse getProfile(UUID userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(
                "User with UUID (" + userId + ") not found in DB during profile getting (REST).",
                "User not found. Sign up or contact support."
        ));

        return userMapper.toProfile(user);
    }

    private boolean applyAndCheckChanges(UpdateProfileRequest request, User user) {
        boolean isSmthChanged = false;

        List<FieldErrorDto> errors = new ArrayList<>();

        if (request.getName() != null && !request.getName().equals(user.getName())) {
            user.setName(request.getName());
            isSmthChanged = true;
        }

        if (request.getSurname() != null && !request.getSurname().equals(user.getSurname())) {
            user.setSurname(request.getSurname());
            isSmthChanged = true;
        }

        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            Optional<User> userNickname = userRepo.findByNickname(request.getNickname());
            if (userNickname.isPresent()) {
                errors.add(new FieldErrorDto("nickname", "This nickname already exists"));
            } else {
                user.setNickname(request.getNickname());
                isSmthChanged = true;
            }
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            Optional<User> userPhoneNumber = userRepo.findByPhoneNumber(request.getPhoneNumber());
            if (userPhoneNumber.isPresent()) {
                errors.add(new FieldErrorDto("phone number", "This phone number already exists"));
            } else {
                user.setPhoneNumber(request.getPhoneNumber());
                isSmthChanged = true;
            }
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            Optional<User> userEmail = userRepo.findByEmail(request.getEmail());
            if (userEmail.isPresent()) {
                errors.add(new FieldErrorDto("email", "This email already exists"));
            } else {
                user.setEmail(request.getEmail());
                isSmthChanged = true;
            }
        }

        if (!errors.isEmpty()) {
            throw new AlreadyExistsException(errors);
        }

        return isSmthChanged;
    }
}