package com.evofun.userservice.feature.profile.app.mapper;

import com.evofun.userservice.shared.domain.model.User;
import com.evofun.userservice.feature.profile.api.response.ProfileResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public ProfileResponse toProfile(User user) {
        return new ProfileResponse(
                user.getName(),
                user.getSurname(),
                user.getNickname(),
                user.getPhoneNumber(),
                user.getEmail()
        );
    }
}