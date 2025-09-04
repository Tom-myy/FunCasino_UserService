package com.evofun.userservice.common.db;

import com.evofun.userservice.common.db.entity.User;
import com.evofun.userservice.common.dto.UserInternalDto;
import com.evofun.userservice.common.error.FieldErrorDto;
import com.evofun.userservice.common.mapper.UserMapper;
import com.evofun.userservice.rest.dto.request.TransferBetweenBalancesDto;
import com.evofun.userservice.rest.dto.request.UpdateProfileRequestDto;
import com.evofun.userservice.rest.dto.response.UserProfileResponse;
import com.evofun.userservice.rest.exception.AlreadyExistsException;
import com.evofun.userservice.rest.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Value("${money-service.money-url}")
    private String moneyServiceUrl;

    private final UserRepository evoUserRepo;
    private final UserMapper userMapper;

    public UserService(UserRepository evoUserRepo, UserMapper userMapper) {
        this.evoUserRepo = evoUserRepo;
        this.userMapper = userMapper;
    }

    public Optional<User> findByNickname(String nickname) {
        return evoUserRepo.findByNickname(nickname);
    }

    public Optional<User> findByEmail(String email) {
        return evoUserRepo.findByEmail(email);
    }

    public Optional<User> findById(UUID id) {
        return evoUserRepo.findById(id);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return evoUserRepo.findByPhoneNumber(phoneNumber);
    }

    public User saveUser(User user) {
        return evoUserRepo.save(user);
    }

/*    public List<UserInternalDto> updateUsersAfterGame(List<UserInternalDto> userDtoList) {
        for (UserInternalDto userDto : userDtoList) {
            User user = evoUserRepo.findById(userDto.getUserUUID())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setBalance(user.getBalance().add(userDto.getBalanceDelta()));

            evoUserRepo.save(user);

            userDto.setBalanceDelta(BigDecimal.ZERO);
        }

        return userDtoList.stream()
                .map(user -> evoUserRepo.findById(user.getUserUUID())
                        .orElseThrow(() -> new RuntimeException("User not found")))
                .map(user -> new UserInternalDto(
                        user.getUserId(),
                        user.getName(),
                        user.getSurname(),
                        user.getNickname(),
//                        user.getBalance(),
                        BigDecimal.ZERO
                ))
                .toList();

    }*/

    public UserProfileResponse getProfile(UUID userId) {
        User user = evoUserRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(
                "User with UUID (" + userId + ") not found in DB during profile getting (REST).",
                "User not found. Sign up or contact support."
        ));

        return userMapper.toProfile(user);
    }

    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequestDto request) {
        User user = evoUserRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(
                "User with UUID (" + userId + ") not found in DB during profile updating (REST).",
                "User not found. Sign up or contact support."
        ));

        boolean isSmthChanged = applyAndCheckChanges(request, user);

        if (isSmthChanged)
            return userMapper.toProfile(saveUser(user));

        return userMapper.toProfile(user);
    }

    private boolean applyAndCheckChanges(UpdateProfileRequestDto request, User user) {
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
            Optional<User> userNickname = findByNickname(request.getNickname());
            if (userNickname.isPresent()) {
                errors.add(new FieldErrorDto("nickname", "This nickname already exists"));
            } else {
                user.setNickname(request.getNickname());
                isSmthChanged = true;
            }
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            Optional<User> userPhoneNumber = findByPhoneNumber(request.getPhoneNumber());
            if (userPhoneNumber.isPresent()) {
                errors.add(new FieldErrorDto("phone number", "This phone number already exists"));
            } else {
                user.setPhoneNumber(request.getPhoneNumber());
                isSmthChanged = true;
            }
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            Optional<User> userEmail = findByEmail(request.getEmail());
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

    public void transferBetweenBalances(TransferBetweenBalancesDto request) {
        WebClient client = WebClient.create(moneyServiceUrl);

        client.post()
                .uri(moneyServiceUrl + "/api/internal/transferBetweenBalances")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
