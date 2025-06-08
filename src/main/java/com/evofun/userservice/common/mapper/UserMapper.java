package com.evofun.userservice.common.mapper;

import com.evofun.userservice.common.db.entity.User;
import com.evofun.userservice.common.dto.UserInternalDto;
import com.evofun.userservice.rest.dto.response.UserProfileResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UserMapper {
    public UserInternalDto convertToUserInternalDto(User user) {
        return new UserInternalDto(
                user.getUserId(),
                user.getName(),
                user.getSurname(),
                user.getNickname(),
                user.getBalance(),
                BigDecimal.ZERO
        );
    }

    public UserProfileResponse toProfile(User user) {
        return new UserProfileResponse(
                user.getUserId(),
                user.getName(),
                user.getSurname(),
                user.getNickname(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getBalance()
        );
    }
}
