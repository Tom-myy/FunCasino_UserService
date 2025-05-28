package com.evofun.userservice.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequestDto {
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

    public UpdateProfileRequestDto() {}

    public UpdateProfileRequestDto(String name, String surname, String nickname, String phoneNumber, String email) {
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}
