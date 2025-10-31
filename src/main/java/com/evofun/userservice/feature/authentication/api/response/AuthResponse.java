package com.evofun.userservice.feature.authentication.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({ "accessToken", "tokenType", "nickname" })
public class AuthResponse {
    private final String accessToken;
    private final String tokenType = "Bearer";
    private final String nickName;

    public AuthResponse(String s, String nickName) {
        this.accessToken = s;
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "JwtResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", nickname='" + nickName + '\'' +
                '}';
    }
}