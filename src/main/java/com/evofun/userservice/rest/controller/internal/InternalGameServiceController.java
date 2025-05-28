package com.evofun.userservice.rest.controller.internal;

import com.evofun.userservice.common.db.UserService;
import com.evofun.userservice.common.db.entity.User;
import com.evofun.userservice.common.dto.UserInternalDto;
import com.evofun.userservice.common.mapper.UserMapper;
import com.evofun.userservice.rest.dto.request.UpdateProfileRequestDto;
import com.evofun.userservice.rest.dto.response.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/internal")
public class InternalGameServiceController {
    private final UserService userService;
    private final UserMapper userMapper;

    public InternalGameServiceController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/userById/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        Optional<User> optionalUser = userService.findById(id);

        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(userMapper.convertToUserInternalDto(optionalUser.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/updateUsersAfterGame")
    public ResponseEntity<List<UserInternalDto>> updateUsersAfterGame(@RequestBody List<UserInternalDto> userDtoList) {
        return ResponseEntity.ok(userService.updateUsersAfterGame(userDtoList));
    }

    @PatchMapping("/update")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody UpdateProfileRequestDto request) {

        UserProfileResponse updated = userService.updateProfile(userId, request);
        return ResponseEntity.ok(updated);
    }
}
