package com.evofun.userservice.common.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class UserInternalDto {
    private UUID userUUID;
    private String name;
    private String surname;
    private String nickname;
    private BigDecimal balanceDelta;

    public UserInternalDto(UUID userUUID, String name, String surname, String nickName,  BigDecimal balanceDelta) {
        this.userUUID = userUUID;
        this.name = name;
        this.surname = surname;
        this.nickname = nickName;
        this.balanceDelta = balanceDelta;
    }
}
