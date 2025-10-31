package com.evofun.userservice.feature.authentication.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    @NotBlank
    private String login;
    @NotBlank
    private String pass;

    public AuthRequest() {}

    public AuthRequest(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }
}