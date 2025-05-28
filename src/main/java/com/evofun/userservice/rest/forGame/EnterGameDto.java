package com.evofun.userservice.rest.forGame;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnterGameDto {
    private String connectionUrl;
    private String token;
}
