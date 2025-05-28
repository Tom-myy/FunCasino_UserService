package com.evofun.userservice.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.UUID;

@Getter
@JsonPropertyOrder({ "token", "userId", "nickname" })
public class JwtResponse {
    private final String token;
    private final UUID userId;//TODO remove!
    private final String nickName;

    public JwtResponse(String s, UUID userID, String nickName) {
        this.token = s;
        this.userId = userID;
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "JwtResponse{" +
                "token='" + token + '\'' +
                ", userId=" + userId +
                ", nickname='" + nickName + '\'' +
                '}';
    }
}

