package com.evofun.userservice.feature.profile.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
    private String name;
    private String surname;
    private String nickname;
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be in valid format"
    )
    private String phoneNumber;
    @Email(message = "Email must be in valid format")
    private String email;

    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String name, String surname, String nickname, String phoneNumber, String email) {
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}