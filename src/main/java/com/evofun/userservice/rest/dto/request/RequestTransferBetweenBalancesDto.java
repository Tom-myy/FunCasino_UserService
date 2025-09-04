package com.evofun.userservice.rest.dto.request;

import com.evofun.userservice.BalanceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestTransferBetweenBalancesDto {
    @NotNull
    private BalanceType from;
    @NotNull
    private BalanceType to;
    @NotNull
    @Positive
    private BigDecimal amount;
}
