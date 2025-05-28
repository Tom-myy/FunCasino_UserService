package com.evofun.userservice.rest.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class UserProfileResponse {
    private UUID userId;//TODO remove!
    private String name;
    private String surname;
    private String nickname;
    private String phoneNumber;
    private String email;
    private BigDecimal balance;

    public UserProfileResponse() {}

    public UserProfileResponse(UUID userId, String name, String surname, String nickname, String phoneNumber, String email, BigDecimal balance) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.balance = balance;
    }
}
