package com.evofun.userservice.rest.controller.publicc;

import com.evofun.userservice.common.db.UserService;
import com.evofun.userservice.rest.dto.request.RequestTransferBetweenBalancesDto;
import com.evofun.userservice.rest.dto.request.TransferBetweenBalancesDto;
import com.evofun.userservice.rest.dto.request.UpdateProfileRequestDto;
import com.evofun.userservice.rest.dto.response.UserProfileResponse;
import com.evofun.userservice.security.jwt.JwtUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/profile")
public class ProfileController {
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal JwtUser principal) {

        UserProfileResponse profile = userService.getProfile(principal.getUserId());
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/update")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @AuthenticationPrincipal JwtUser principal,
            @Valid @RequestBody UpdateProfileRequestDto request) {

        UserProfileResponse updated = userService.updateProfile(principal.getUserId(), request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/transferBetweenBalances")//TODO handle RuntimeEx from internal controller through RestControllerAdvice
    public ResponseEntity<?> transferBetweenBalances(
            @AuthenticationPrincipal JwtUser principal,
            @Valid @RequestBody RequestTransferBetweenBalancesDto request) {

        TransferBetweenBalancesDto tbbd = new TransferBetweenBalancesDto(
                principal.getUserId(),
                request.getFrom(),
                request.getTo(),
                request.getAmount());
        userService.transferBetweenBalances(tbbd);

        return ResponseEntity.ok().build();
    }
}
