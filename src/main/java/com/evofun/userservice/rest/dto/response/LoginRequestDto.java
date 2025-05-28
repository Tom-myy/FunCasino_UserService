package com.evofun.userservice.rest.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    @NotBlank
    private String login;
    @NotBlank
    private String pass;

    public LoginRequestDto() {}

    public LoginRequestDto(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }
}
