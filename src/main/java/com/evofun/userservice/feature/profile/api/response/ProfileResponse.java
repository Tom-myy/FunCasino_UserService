package com.evofun.userservice.feature.profile.api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponse {
    private String name;
    private String surname;
    private String nickname;
    private String phoneNumber;
    private String email;

    public ProfileResponse() {}

    public ProfileResponse(String name, String surname, String nickname, String phoneNumber, String email) {
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}