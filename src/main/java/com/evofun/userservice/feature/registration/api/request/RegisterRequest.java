package com.evofun.userservice.feature.registration.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Surname is required")
    private String surname;
    @NotBlank(message = "Nickname is required")
    private String nickname;
    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be in valid format"
    )
    private String phoneNumber;
    @Email(message = "Email must be in valid format")
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Pass is required")
    private String pass;

    public RegisterRequest() {}

    public RegisterRequest(String name, String surname, String nickname, String phoneNumber, String email, String pass) {
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.pass = pass;
    }
}